package org.diagnosis.algorithms.reconstruction.graph;

import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.entities.Hit;
import org.diagnosis.algorithms.entities.HitVector;
import org.diagnosis.algorithms.reconstruction.GraphWeightCalculator;
import org.diagnosis.algorithms.reconstruction.graph.nodes.EndNode;
import org.diagnosis.algorithms.reconstruction.graph.nodes.Node;
import org.diagnosis.algorithms.reconstruction.graph.nodes.NopNode;
import org.diagnosis.algorithms.reconstruction.graph.nodes.StartNode;
import org.diagnosis.algorithms.reconstruction.techniques.bruteforce.DAGFindAllPathsPaths;
import org.diagnosis.algorithms.reconstruction.techniques.bruteforce.ExecutionPath;
import org.diagnosis.algorithms.walker.weightModifiers.WeightModifier;
import org.diagnosis.evaluation.ground_truth.GroundTruth;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ExecutionGraph {
    public Graph<String, DefaultEdge> graph;
    public Map<String, Node> nodes = new HashMap<>();
    Deque<String> callStack = new ArrayDeque<>();
    public String startNode;
    public String endNode;
    CachedNode cachedNode;
    Random rand = new Random(10);
    Map<DefaultEdge, Integer> weights;
    Map<Node, Float> probabilities;
    Map<Node, List<Float>> candidateProbabilities;
    Set<Component> partialTrace;
    Set<Component> fullTrace;
    HitVector fullHitVector;
    Set<Component> reconstructedTrace;

    Logger logger = LoggerFactory.getLogger(ExecutionGraph.class);

    public ExecutionGraph(Node rootNode) {
        this.graph = new DirectedAcyclicGraph<>(DefaultEdge.class);
        this.addNode(rootNode);
    }

    public Node getNode(String nodeReference) {
        return this.nodes.get(nodeReference);
    }

    public void addNode(Node node) {
        String nodeReference = node.toString();
        nodes.put(nodeReference, node);
        this.graph.addVertex(nodeReference);
    }

    public void addEdge(Node source, Node target) {
        String sourceNodeReference = source.toString();
        String targetNodeReference = target.toString();
        try {
            this.graph.addEdge(sourceNodeReference, targetNodeReference);
        } catch (IllegalArgumentException e) {
            logger.error("Could not add edge from {} to {}", sourceNodeReference, targetNodeReference);
        } catch (StackOverflowError e) {
            logger.error("Stack overflow error while adding edge from {} to {}", sourceNodeReference, targetNodeReference);
        }
    }

    public void addEdge(Node source, String target) {
        String sourceNodeReference = source.toString();
        this.graph.addEdge(sourceNodeReference, target);
    }

    public void addEdge(String source, Node target) {
        String targetNodeReference = target.toString();
        this.graph.addEdge(source, targetNodeReference);
    }

    private Set<String> getStartNodes() {
        Set<String> startNodes = new HashSet<>();
        for (String node : this.graph.vertexSet()) {
            if (this.graph.inDegreeOf(node) == 0) {
                startNodes.add(node);
            }
        }
        return startNodes;
    }

    private Set<String> getEndNodes() {
        Set<String> endNodes = new HashSet<>();
        for (String node : this.graph.vertexSet()) {
            if (this.graph.outDegreeOf(node) == 0) {
                endNodes.add(node);
            }
        }
        return endNodes;
    }

    public void addStartEndNodes() {
        Set<String> startNodes = this.getStartNodes();
        Set<String> endNodes = this.getEndNodes();

        assert startNodes.size() == 1;
        assert endNodes.size() >= 1;

        Node startNode = new StartNode();
        Node endNode = new EndNode();

        this.addNode(startNode);
        this.addNode(endNode);

        for (String node : startNodes) {
            this.addEdge(startNode, node);
        }
        this.startNode = startNode.toString();

        for (String node : endNodes) {
            this.addEdge(node, endNode);
        }

        this.endNode = endNode.toString();
    }

    public List<Component> filterComponentsInTrace(List<Component> components) {
        return components.stream().filter(c -> this.nodes.values().stream().map(Node::toComponent).collect(Collectors.toList()).contains(c)).collect(Collectors.toList());
    }

    public void setPartialTraces(HitVector partialTrace) {
        this.partialTrace = partialTrace.getHitComponents();
    }

    public String getReconstructionTraceGraph(Map<Component, String> componentMapper, HitVector reconstructedTrace) {
        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>();
        exporter.setVertexAttributeProvider(s -> {
            Map<String, Attribute> map = new HashMap<>();
            if (this.fullTrace != null) {
                if (this.fullTrace.contains(this.nodes.get(s).toComponent())) {
                    if (reconstructedTrace.getHitComponents().contains(this.nodes.get(s).toComponent())) {
                        map.put("style", DefaultAttribute.createAttribute("filled"));
                        map.put("fillcolor", DefaultAttribute.createAttribute("darkseagreen1"));

                        if (this.partialTrace.contains(this.nodes.get(s).toComponent())) {
                            map.put("style", DefaultAttribute.createAttribute("filled"));
                            map.put("fillcolor", DefaultAttribute.createAttribute("lightpink:darkseagreen1"));

                        }

                    } else if (this.partialTrace.contains(this.nodes.get(s).toComponent())) {
                        map.put("style", DefaultAttribute.createAttribute("filled"));
                        map.put("fillcolor", DefaultAttribute.createAttribute("lightpink"));
                    }

                } else {
                    map.put("style", DefaultAttribute.createAttribute("filled"));
                    map.put("fillcolor", DefaultAttribute.createAttribute("lightgray"));
                }
            }

            if (probabilities != null && this.probabilities.containsKey(this.nodes.get(s))) {
                if (componentMapper.containsKey(this.nodes.get(s).toComponent()) && componentMapper.get(this.nodes.get(s).toComponent()).equals("c17"))
                    System.out.println("c17");
                map.put("label", DefaultAttribute.createAttribute(String.format("%s (%.2f)", componentMapper.get(this.nodes.get(s).toComponent()), this.probabilities.get(this.nodes.get(s)))));
            } else {
                map.put("label", DefaultAttribute.createAttribute(componentMapper.get(this.nodes.get(s).toComponent())));
            }

            return map;
        });

        exporter.setEdgeAttributeProvider(edge -> {
            Map<String, Attribute> map = new HashMap<>();
            if (weights != null) {
                Integer weight = this.weights.get(edge); // assuming edgeWeights is accessible in this context
                if (weight != null) {
                    map.put("label", DefaultAttribute.createAttribute(weight.toString()));
                }
            }
            return map;
        });

        StringWriter writer = new StringWriter();
        exporter.exportGraph(this.graph, writer);
        return writer.toString();
    }

    public String getFullTraceGraph(Map<Component, String> componentMapper, boolean fullNames) {
        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>();
        exporter.setVertexAttributeProvider(s -> {
            Map<String, Attribute> map = new HashMap<>();
            if (this.fullTrace != null) {
                if (this.fullTrace.contains(this.nodes.get(s).toComponent())) {
                    map.put("style", DefaultAttribute.createAttribute("filled"));
                    map.put("fillcolor", DefaultAttribute.createAttribute("lightcyan"));
                }
            }

            if (fullNames)
                map.put("label", DefaultAttribute.createAttribute(String.format("(%s) %s", componentMapper.get(this.nodes.get(s).toComponent()), this.nodes.get(s).toString())));
            else
                if (this.fullHitVector != null && this.fullHitVector.getHit(this.nodes.get(s).toComponent()) != null)
                    map.put("label", DefaultAttribute.createAttribute(String.format("%s (%.2f hits)", DefaultAttribute.createAttribute(componentMapper.get(this.nodes.get(s).toComponent())), this.fullHitVector.getHit(this.nodes.get(s).toComponent()).getNumberOfHits())));
                else
                    map.put("label", DefaultAttribute.createAttribute(componentMapper.get(this.nodes.get(s).toComponent())));
            return map;
        });

        StringWriter writer = new StringWriter();
        exporter.exportGraph(this.graph, writer);
        return writer.toString();
    }

    public String getPartialTraceGraph(Map<Component, String> componentMapper, boolean fullNames) {
        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>();
        exporter.setVertexAttributeProvider(s -> {
            Map<String, Attribute> map = new HashMap<>();
            if (this.fullTrace != null) {
                if (this.fullTrace.contains(this.nodes.get(s).toComponent())) {
                    if (this.partialTrace.contains(this.nodes.get(s).toComponent())) {
                        map.put("style", DefaultAttribute.createAttribute("filled"));
                        map.put("fillcolor", DefaultAttribute.createAttribute("lightpink"));
                    }
                } else {
                    map.put("style", DefaultAttribute.createAttribute("filled"));
                    map.put("fillcolor", DefaultAttribute.createAttribute("lightgray"));
                }
            }

            if (fullNames)
                map.put("label", DefaultAttribute.createAttribute(this.nodes.get(s).toString()));
            else
                map.put("label", DefaultAttribute.createAttribute(componentMapper.get(this.nodes.get(s).toComponent())));
            return map;
        });

        StringWriter writer = new StringWriter();
        exporter.exportGraph(this.graph, writer);
        return writer.toString();
    }

    @Override
    public String toString() {
        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>();
        exporter.setVertexAttributeProvider(s -> {
            Map<String, Attribute> map = new HashMap<>();
            if (this.fullTrace != null) {
                if (this.fullTrace.contains(this.nodes.get(s).toComponent())) {
                    map.put("style", DefaultAttribute.createAttribute("filled"));
                    map.put("fillcolor", DefaultAttribute.createAttribute("lightcyan"));
                }
            }

            if (this.partialTrace != null) {
                if (this.partialTrace.contains(this.nodes.get(s).toComponent())) {
                    if (this.fullTrace != null) {
                        if (this.fullTrace.contains(this.nodes.get(s).toComponent())) {
                            map.put("style", DefaultAttribute.createAttribute("filled"));
                            map.put("fillcolor", DefaultAttribute.createAttribute("lightskyblue"));
                        }
                    }

                }
            }

            if (this.reconstructedTrace != null) {
                if (this.reconstructedTrace.contains(this.nodes.get(s).toComponent())) {
                    map.put("style", DefaultAttribute.createAttribute("filled"));
                    map.put("fillcolor", DefaultAttribute.createAttribute("lightpink"));
                }
            }

            if (probabilities != null && this.probabilities.containsKey(this.nodes.get(s))) {
                map.put("label", DefaultAttribute.createAttribute(String.format("%.2f - (%s)", this.probabilities.get(this.nodes.get(s)), this.candidateProbabilities.get(this.nodes.get(s)).stream().map(Object::toString).collect(Collectors.joining(", ")))));
                if (this.probabilities.get(this.nodes.get(s)) == 1.0f) {
                    if (this.fullTrace != null) {
                        map.put("style", DefaultAttribute.createAttribute("filled"));
                        if (this.fullTrace.contains(this.nodes.get(s).toComponent())) {
                            map.put("fillcolor", DefaultAttribute.createAttribute("springgreen"));
                        } else {
                            map.put("fillcolor", DefaultAttribute.createAttribute("pink"));
                        }
                    }
                }
            } else {
                map.put("label", DefaultAttribute.createAttribute(this.nodes.get(s).toComponent().toString()));
                //map.put("label", DefaultAttribute.createAttribute(s));
            }




            return map;
        });

        exporter.setEdgeAttributeProvider(edge -> {
            Map<String, Attribute> map = new HashMap<>();
            if (weights != null) {
                Integer weight = this.weights.get(edge); // assuming edgeWeights is accessible in this context
                if (weight != null) {
                    map.put("label", DefaultAttribute.createAttribute(weight.toString()));
                }
            }
            return map;
        });

        StringWriter writer = new StringWriter();
        exporter.exportGraph(this.graph, writer);
        return writer.toString();
    }

    public String toColoredString(GroundTruth groundTruth) {
        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>();

        exporter.setVertexAttributeProvider(s -> {
            Map<String, Attribute> map = new HashMap<>();
            map.put("label", DefaultAttribute.createAttribute(s));
            // Check if this node is the one we want to color differently
            Node node = nodes.get(s);
            for (Component component : groundTruth.getComponents()) {
                if (node.toComponent().equals(component)) {
                    // Change color to the specified color
                    map.put("color", DefaultAttribute.createAttribute("red"));
                    map.put("style", DefaultAttribute.createAttribute("filled"));
                }
            }
            return map;
        });

        StringWriter writer = new StringWriter();
        exporter.exportGraph(graph, writer);
        return writer.toString();
    }


    public void removeNOPNodes() {
        Set<String> nopNodes = new HashSet<>();

        // Identify NOP nodes
        for (String vertexReference : this.graph.vertexSet()) {
            Node vertex = this.nodes.get(vertexReference);
            if (vertex instanceof NopNode) {
                nopNodes.add(vertexReference);
            }
        }

        // Process each NOP node
        for (String nopNode : nopNodes) {
            Set<DefaultEdge> incomingEdges = new HashSet<>(this.graph.incomingEdgesOf(nopNode));
            Set<DefaultEdge> outgoingEdges = new HashSet<>(this.graph.outgoingEdgesOf(nopNode));

            // Create new edges bypassing the NOP node
            for (DefaultEdge incoming : incomingEdges) {
                String source = this.graph.getEdgeSource(incoming);
                for (DefaultEdge outgoing : outgoingEdges) {
                    String target = this.graph.getEdgeTarget(outgoing);
                    this.graph.addEdge(source, target);
                }
            }

            // Remove the NOP node
            this.graph.removeVertex(nopNode);
            this.nodes.remove(nopNode);
        }
    }

    public List<ExecutionPath> getPaths() {
        logger.debug("Finding all paths from {} to {}", this.startNode, this.endNode);
        List<List<String>> paths = DAGFindAllPathsPaths.findAllShortestPathsMemoryEfficient(graph, startNode, endNode);
        logger.debug("Found {} paths", paths.size());
        return paths.parallelStream().map(path -> new ExecutionPath(path, this.nodes, this.graph)).collect(Collectors.toList());
    }

    public void pruneGraph(HitVector hitVector) {
        Set<String> packageNames = hitVector.stream().map(Hit::getPackageName).collect(Collectors.toSet());
        DirectedAcyclicGraph<String, DefaultEdge> newGraph = new DirectedAcyclicGraph<>(DefaultEdge.class);
        Map<String, Node> newNodesList = new HashMap<>();

        // Add all relevant vertices first
        for (String vertexReference : this.graph.vertexSet()) {
            Node vertex = this.nodes.get(vertexReference);
            if (packageNames.contains(vertex.getPackageName())) {
                newGraph.addVertex(vertexReference);
                newNodesList.put(vertexReference, vertex);
            }
        }

        // Add edges, skipping missing intermediate nodes
        for (String vertexReference : this.graph.vertexSet()) {
            if (packageNames.contains(this.nodes.get(vertexReference).getPackageName())) {
                for (DefaultEdge incomingEdge : this.graph.incomingEdgesOf(vertexReference)) {
                    String ancestor = this.graph.getEdgeSource(incomingEdge);
                    if (packageNames.contains(this.nodes.get(ancestor).getPackageName())) {
                        newGraph.addEdge(ancestor, vertexReference);
                    } else {
                        // Connect ancestors of the missing node directly to the current node
                        connectAncestors(ancestor, vertexReference, newGraph, packageNames);
                    }
                }
            }
        }

        this.graph = newGraph;
        this.nodes = newNodesList;
    }

    private void connectAncestors(String ancestor, String descendant,
                                  DirectedAcyclicGraph<String, DefaultEdge> graph,
                                  Set<String> packageNames) {
        for (DefaultEdge edge : this.graph.incomingEdgesOf(ancestor)) {
            String ancestorOfAncestor = this.graph.getEdgeSource(edge);
            if (packageNames.contains(this.nodes.get(ancestorOfAncestor).getPackageName())) {
                graph.addVertex(ancestorOfAncestor); // Ensure vertex is added
                graph.addEdge(ancestorOfAncestor, descendant);
            } else {
                connectAncestors(ancestorOfAncestor, descendant, graph, packageNames);
            }
        }
    }

    public void addToStack(Node node) {
        this.callStack.push(node.getSignature());
    }

    public void popFromStack() {
        this.callStack.pop();
    }

    public boolean isNotInStack(Node node) {
        return !this.callStack.contains(node.getSignature());
    }

    public String peekStack() {
        return this.callStack.peek();
    }

    public int sizeStack() {
        return this.callStack.size();
    }

    public int hashStack() {
        return this.callStack.stream().map(String::hashCode).reduce(Objects::hash).orElse(0);
    }

    public void save(Path executionGraphPath, GroundTruth groundTruth) throws IOException {
        if (groundTruth != null) {
            Files.write(executionGraphPath, this.toColoredString(groundTruth).getBytes(StandardCharsets.UTF_8));
        } else {
            Files.write(executionGraphPath, this.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    public Node getNode(Component component) {
        return this.nodes.values().stream().filter(node -> node.toComponent().equals(component)).findFirst().orElse(null);
    }

    public boolean checkNodeInRandomWalk(Node node, List<Component> nodesInPartialTraces, Counter counter) {
        List<DefaultEdge> nextEdges = new ArrayList<>(graph.outgoingEdgesOf(startNode));
        while(nextEdges.size() > 0) {
            DefaultEdge edge = nextEdges.get(rand.nextInt(nextEdges.size()));

            if (nodesInPartialTraces.contains(this.nodes.get(graph.getEdgeTarget(edge)).toComponent())) {
                counter.increment();
            }

            if (graph.getEdgeTarget(edge).equals(endNode)) {
                return false;
            }

            nextEdges = new ArrayList<>(graph.outgoingEdgesOf(graph.getEdgeTarget(edge)));
            if (graph.getEdgeTarget(edge).equals(node.toString())) {
                return true;
            }
        }
        return false;
    }

    public void calculateWeights(HitVector partialTraces) {
        this.weights = GraphWeightCalculator.calculateWeights((DirectedAcyclicGraph<String, DefaultEdge>) this.graph, this.endNode, partialTraces.getHitComponents(), this.nodes);
    }

    public void calculateNodeProbabilities() {
        // Assign probability to each node in the execution graph
        // Keep a stack of all nodes that need exploring, based on the possible incoming edges
        this.probabilities = new HashMap<>();
        Queue<String> nextNodes = new LinkedList<>();
        nextNodes.add(startNode);
        candidateProbabilities = new HashMap<>();
        this.candidateProbabilities.computeIfAbsent(nodes.get(startNode), k -> new ArrayList<>()).add(1.0f);
        Set<String> visitedNodes = new HashSet<>();
        while(nextNodes.size() > 0) {
            String nextNode = nextNodes.poll();
            assert nextNode != null;
            if (visitedNodes.contains(nextNode)) {
                continue;
            }

            if (nextNode.equals(endNode)) {
                if (nextNodes.size() > 0) {
                    continue;
                } else {
                    this.probabilities.put(nodes.get(nextNode), 1.0f);
                    candidateProbabilities.getOrDefault(nodes.get(nextNode), new ArrayList<>()).add(1.0f);
                    return;
                }
            }

            float nodeProbability;
            if (graph.incomingEdgesOf(nextNode).stream().anyMatch(edge -> !visitedNodes.contains(graph.getEdgeSource(edge)))) {
                assert nextNodes.size() > 0;
                nextNodes.add(nextNode);
                continue;
            } else {
                nodeProbability = (float) candidateProbabilities.get(nodes.get(nextNode)).stream().mapToDouble(Float::doubleValue).sum();
                this.probabilities.put(nodes.get(nextNode), nodeProbability);
                visitedNodes.add(nextNode);
            }


            List<DefaultEdge> nextEdges = new ArrayList<>(graph.outgoingEdgesOf(nextNode));
            Map<Integer, List<DefaultEdge>> groupedWeights = nextEdges.stream()
                    .collect(Collectors.groupingBy(this.weights::get));
            Optional<Map.Entry<Integer, List<DefaultEdge>>> maxWeightEdgesEntry = groupedWeights.entrySet().stream().max(Map.Entry.comparingByKey());
            assert maxWeightEdgesEntry.isPresent();
            int maxWeight = maxWeightEdgesEntry.get().getKey();
            List<String> maxWeightNodes = maxWeightEdgesEntry.get().getValue().stream().map(edge -> graph.getEdgeTarget(edge)).collect(Collectors.toList());
            for (String maxWeightNode : maxWeightNodes) {
                candidateProbabilities.computeIfAbsent(nodes.get(maxWeightNode), k -> new ArrayList<>()).add(nodeProbability/maxWeightNodes.size());
                if (!visitedNodes.contains(maxWeightNode) && !nextNodes.contains(maxWeightNode))
                    nextNodes.add(maxWeightNode);
            }
            groupedWeights.remove(maxWeight);
            if (!groupedWeights.isEmpty()) {
                for (List<DefaultEdge> restEdges : groupedWeights.values()) {
                    List<String> restNodes = restEdges.stream().map(edge -> graph.getEdgeTarget(edge)).collect(Collectors.toList());
                    for (String restNode : restNodes) {
                        candidateProbabilities.computeIfAbsent(this.nodes.get(restNode), k -> new ArrayList<>()).add(0.0f);
                        if (!visitedNodes.contains(restNode) && !nextNodes.contains(restNode))
                            nextNodes.add(restNode);
                    }
                }
            }
        }
    }

    public boolean checkNodeInInformedWalk(Node node, List<Component> nodesInPartialTraces, Counter counter) {
        List<DefaultEdge> nextEdges = new ArrayList<>(graph.outgoingEdgesOf(startNode));
        while(nextEdges.size() > 0) {
            Optional<Map.Entry<Integer, List<DefaultEdge>>> maxWeightEdgesEntry = nextEdges.stream()
                    .collect(Collectors.groupingBy(this.weights::get))
                    .entrySet()
                    .stream()
                    .max(Map.Entry.comparingByKey());

            if (maxWeightEdgesEntry.isPresent()) {
                nextEdges = maxWeightEdgesEntry.get().getValue();
            }

            DefaultEdge edge = nextEdges.get(rand.nextInt(nextEdges.size()));

            if (nodesInPartialTraces.contains(this.nodes.get(graph.getEdgeTarget(edge)).toComponent())) {
                counter.increment();
            }

            if (graph.getEdgeTarget(edge).equals(endNode)) {
                return false;
            }

            nextEdges = new ArrayList<>(graph.outgoingEdgesOf(graph.getEdgeTarget(edge)));
            if (graph.getEdgeTarget(edge).equals(node.toString())) {
                return true;
            }
        }
        return false;
    }

    public boolean checkNodeInInformedRandomWalk(Node node, WeightModifier modifier, List<Component> nodesInPartialTraces, Counter counter) {
        List<DefaultEdge> nextEdges = new ArrayList<>(graph.outgoingEdgesOf(startNode));
        Map<DefaultEdge, Integer> cumulativeWeights = new HashMap<>();
        int totalWeight;
        while(nextEdges.size() > 0) {
            cumulativeWeights.clear();
            totalWeight = 0;
            for (DefaultEdge edge : nextEdges) {
                totalWeight += modifier.transform(this.weights.get(edge));
                cumulativeWeights.put(edge, totalWeight);
            }
            int randomValue = totalWeight > 0 ? rand.nextInt(totalWeight) : 0;
            DefaultEdge selectedEdge = nextEdges.get(0);
            for (DefaultEdge edge : nextEdges) {
                if (randomValue < cumulativeWeights.get(edge)) {
                    selectedEdge = edge;
                    break;
                }
            }
            if (nodesInPartialTraces.contains(this.nodes.get(graph.getEdgeTarget(selectedEdge)).toComponent())) {
                counter.increment();
            }
            if (graph.getEdgeTarget(selectedEdge).equals(endNode)) {
                return false;
            }

            nextEdges = new ArrayList<>(graph.outgoingEdgesOf(graph.getEdgeTarget(selectedEdge)));
            if (graph.getEdgeTarget(selectedEdge).equals(node.toString())) {
                return true;
            }
        }
        return false;
    }

    public float getWeightProbability(Node node) {
        try {
            return this.probabilities.get(node);
        } catch (NullPointerException e) {
            return 0.0f;
        }
    }

    public void setFullTrace(HitVector fullTrace) {
        this.fullTrace = fullTrace.getHitComponents();
        this.fullHitVector = fullTrace;
    }

    public void setReconstructedTrace(HitVector reconstructedTrace) {
        this.reconstructedTrace = reconstructedTrace.getHitComponents();
    }




    static class CachedNode {

        private final Graph<String, DefaultEdge> graph;
        private final Map<String, Node> nodes;

        private Set<DefaultEdge> incomingEdges;
        private Set<DefaultEdge> outgoingEdges;
        private Node node;

        public CachedNode(Graph<String, DefaultEdge> graph, Map<String, Node> nodes) {
            this.graph = graph;
            this.nodes = nodes;
        }

        public void remove(Node node) {
            this.incomingEdges = new HashSet<>(graph.incomingEdgesOf(node.toString()));
            this.outgoingEdges = new HashSet<>(graph.outgoingEdgesOf(node.toString()));

            this.graph.removeVertex(node.toString());
            this.nodes.remove(node.toString());
            this.node = node;
        }

        public void rollBack() {
            graph.addVertex(node.toString());
            nodes.put(node.toString(), node);
            incomingEdges.forEach(edge -> graph.addEdge(graph.getEdgeSource(edge), node.toString()));
            outgoingEdges.forEach(edge -> graph.addEdge(node.toString(), graph.getEdgeTarget(edge)));
        }

    }
    public void remove(Node node) {
        this.cachedNode = new CachedNode(this.graph, this.nodes);
        this.cachedNode.remove(node);
    }

    public void rollBack() {
        this.cachedNode.rollBack();
        this.cachedNode = null;
    }

    public void commit() {
        this.cachedNode = null;
    }

    public boolean isComponentInGraph(Component component) {
        return nodes.values().stream().anyMatch(node -> node.toComponent().equals(component));
    }
}
