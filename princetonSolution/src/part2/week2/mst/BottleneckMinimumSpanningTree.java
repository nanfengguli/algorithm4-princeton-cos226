package part2.week2.mst;

import commonutil.QuickSelect;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.UF;
import part2.week2.mst.bottleneckmst.util.Edge;
import part2.week2.mst.bottleneckmst.util.MyEdgeWeightedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * from this quiz question, we allow use the data structure wrote in algs4, to save time.
 */
public class BottleneckMinimumSpanningTree {
    public List<Edge> get(EdgeWeightedGraph graph) {
        MyEdgeWeightedGraph myEdgeWeightedGraph = new MyEdgeWeightedGraph(graph);
        return get(myEdgeWeightedGraph);
    }
    public List<Edge> get(MyEdgeWeightedGraph graph) {
        List<Edge> edges = graph.edges();
        PriorityQueue<Edge> pq = new PriorityQueue<>(edges);
        UF uf = new UF(graph.V());
        List<Edge> result = new ArrayList<>();
        while (!pq.isEmpty() && result.size() < graph.V() - 1) {
            Edge cur = pq.poll();
            int u = cur.either(), v = cur.other(u);
            if (uf.connected(u, v)) continue;
            uf.union(u, v);
            result.add(cur);
        }
        return result;
    }

    public List<Edge> getInLinearTime(EdgeWeightedGraph graph) {
        return msbt(new MyEdgeWeightedGraph(graph));
    }
    public List<Edge> getInLinearTime(MyEdgeWeightedGraph graph) {
        return msbt(graph);
    }

    private List<Edge> msbt(MyEdgeWeightedGraph myEdgeWeightedGraph) {
        List<Edge> result = new ArrayList<>();
        List<Edge> all = myEdgeWeightedGraph.edges();
        if (all.size() == 1) return all;
        Edge median = findMedianInLinearTime(all);
        MyEdgeWeightedGraph smallHalf = new MyEdgeWeightedGraph();
        MyEdgeWeightedGraph bigHalf = new MyEdgeWeightedGraph();
        UF uf = new UF(myEdgeWeightedGraph.V());
        for (Edge e : all) {
            if (e.compareTo(median) <= 0) {
                smallHalf.addEdge(e);
                if (!uf.connected(e.either(), e.other(e.either()))) {
                    uf.union(e.either(), e.other(e.either()));
                    result.add(e);
                }
            } else {
                bigHalf.addEdge(e);
            }
        }
        if (uf.count() == 1) {
            return msbt(smallHalf);
        }
        MyEdgeWeightedGraph newGraph = buildNewGraph(bigHalf, uf);
        result.addAll(msbt(newGraph));
        return result;
    }

    private MyEdgeWeightedGraph buildNewGraph(MyEdgeWeightedGraph bigHalf, UF uf) {
        MyEdgeWeightedGraph newGraph = new MyEdgeWeightedGraph();
        int cnt = 0;
        Map<Integer, Integer> oldId2NewId = new HashMap<>();
        for (Edge e : bigHalf.edges()) {
            int v = e.v(), w = e.w();
            int parentV = uf.find(v), parentW = uf.find(w);
            if (!oldId2NewId.containsKey(parentV)) {
                oldId2NewId.put(parentV, cnt++);
            }
            e.setV(oldId2NewId.get(parentV));
            if (!oldId2NewId.containsKey(parentW)) {
                oldId2NewId.put(parentW, cnt++);
            }
            e.setW(oldId2NewId.get(parentW));
            newGraph.addEdge(e);
        }
        return newGraph;
    }

    private Edge findMedianInLinearTime(List<Edge> edges) {
        return QuickSelect.findMedian(edges);
    }

}
