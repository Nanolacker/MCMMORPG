package com.mcmmorpg.common.ai;

import java.util.ArrayList;
import java.util.List;

public class Path {

	private List<PathNode> nodes;
	private PathStatus status;

	public Path() {
		this.nodes = new ArrayList<>();
	}

	public List<PathNode> getNodes() {
		return nodes;
	}

	public PathStatus getStatus() {

	}

}
