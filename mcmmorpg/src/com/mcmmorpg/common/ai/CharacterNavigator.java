package com.mcmmorpg.common.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.util.Debug;

public class CharacterNavigator {
    private static final double UPDATE_PERIOD = 1.0;

    private final CharacterPathFollower pathFollower;
    private Location destination;
    private RepeatingTask updateTask;
    private double jumpHeight = 1.25;
    private boolean canClimbLadders = true;;

    public CharacterNavigator(CharacterPathFollower pathFollower) {
        this.pathFollower = pathFollower;
        this.destination = null;
        updateTask = new RepeatingTask(UPDATE_PERIOD) {
            @Override
            protected void run() {
                update();
            }
        };
    }

    private void update() {
        Path path = findPath();
        pathFollower.followPath(path);
    }

    public boolean isEnabled() {
        return updateTask.isScheduled();
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
        if (destination == null) {
            if (updateTask.isScheduled()) {
                updateTask.cancel();
            }
        } else {
            if (!updateTask.isScheduled()) {
                updateTask.schedule();
            }
        }
    }

    private Path findPath() {
        Location blockDestination = destination.getBlock().getLocation().add(0.5, 0.0, 0.5);
        Map<Location, Node> nodes = new HashMap<>();
        List<Node> openNodes = new ArrayList<>();
        Set<Node> closedNodes = new HashSet<>();

        Location blockStart = pathFollower.getCharacter().getLocation().getBlock().getLocation().add(0.5, 0.0, 0.5);
        Node startNode = new Node(blockStart);
        nodes.put(blockStart, startNode);
        openNodes.add(startNode);

        while (!openNodes.isEmpty() && openNodes.size() < 1000) {
            Node current = nodeWithLowestFCost(openNodes);
            openNodes.remove(current);
            closedNodes.add(current);

            if (current.location.equals(blockDestination)) {
                return retracePath(current);
            }

            Node[] neighbors = current.getNeighbors(nodes);
            for (Node neighbor : neighbors) {
                boolean traversable = isTraversable(neighbor, current);
                if (!traversable || closedNodes.contains(neighbor)) {
                    continue;
                }

                double costToNeighbor = current.gCost + current.location.distanceSquared(neighbor.location);
                if (costToNeighbor < neighbor.gCost || !openNodes.contains(neighbor)) {
                    neighbor.gCost = costToNeighbor;
                    neighbor.hCost = neighbor.location.distanceSquared(blockDestination);
                    neighbor.parent = current;

                    if (!openNodes.contains(neighbor)) {
                        openNodes.add(neighbor);
                    }
                }
            }
        }
        return null;
    }

    private Path retracePath(Node endNode) {
        List<Location> waypoints = new ArrayList<>();
        Node current = endNode;
        while (current.parent != null) {
            waypoints.add(current.location);
            current = current.parent;
        }
        Collections.reverse(waypoints);
        return new Path(waypoints.toArray(new Location[waypoints.size()]));
    }

    private Node nodeWithLowestFCost(List<Node> nodes) {
        Node nodeWithLowestCost = nodes.get(0);
        for (int i = 1; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if (node.getFCost() < nodeWithLowestCost.getFCost()) {
                nodeWithLowestCost = node;
            }
        }
        return nodeWithLowestCost;
    }

    private boolean isTraversable(Node node, Node previousNode) {
        Location location = node.location;
        Location previousLocation = previousNode.location;
        Block block = location.getBlock();
        if (block.getType() == Material.LADDER) {
            return canClimbLadders;
        }
        if (location.getX() != previousLocation.getX() && location.getZ() != previousLocation.getZ()) {
            // diagnol movement
            double height = Math.ceil(pathFollower.getCharacter().getHeight());
            for (int y = 0; y < height; y++) {
                // if (there is a block occupying
                Location l1 = new Location(location.getWorld(), previousLocation.getX(), previousLocation.getY() + y,
                        location.getZ());
                Location l2 = new Location(location.getWorld(), location.getX(), previousLocation.getY() + y,
                        previousLocation.getZ());
                if (!l1.getBlock().isPassable() || !l2.getBlock().isPassable()) {
                    return false;
                }
            }
        }
        return node.location.getBlock().isPassable()
                && !node.location.clone().subtract(0, 1, 0).getBlock().isPassable();
    }

    private static class Node {
        Location location;
        Node parent;
        double gCost;
        double hCost;

        Node(Location location) {
            this.location = location;
        }

        double getFCost() {
            return gCost + hCost;
        }

        Node[] getNeighbors(Map<Location, Node> nodes) {
            Node[] neighbors = new Node[26];
            int index = 0;
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) {
                            continue;
                        }
                        Location neighborLocation = location.clone().add(x, y, z);
                        if (!nodes.containsKey(neighborLocation)) {
                            nodes.put(neighborLocation, new Node(neighborLocation));
                        }
                        Node node = nodes.get(neighborLocation);
                        neighbors[index] = node;
                        index++;
                    }
                }
            }
            return neighbors;
        }
    }
}
