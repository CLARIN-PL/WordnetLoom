package pl.edu.pwr.wordnetloom.ui.visualisation;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.visualization.control.LayoutScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.control.ViewScalingControl;
import edu.uci.ics.jung.visualization.decorators.ConstantDirectionalEdgeValueTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.picking.ShapePickSupport;
import org.apache.commons.collections15.Transformer;
import pl.edu.pwr.wordnetloom.application.utils.Loggable;
import pl.edu.pwr.wordnetloom.common.dto.DataEntry;
import pl.edu.pwr.wordnetloom.common.model.NodeDirection;
import pl.edu.pwr.wordnetloom.synset.model.Synset;
import pl.edu.pwr.wordnetloom.synsetrelation.model.SynsetRelation;
import pl.edu.pwr.wordnetloom.ui.visualisation.control.ViwnGraphViewModalGraphMouse;
import pl.edu.pwr.wordnetloom.ui.visualisation.decorators.ViwnEdgeStrokeTransformer;
import pl.edu.pwr.wordnetloom.ui.visualisation.decorators.ViwnVertexToolTipTransformer;
import pl.edu.pwr.wordnetloom.ui.visualisation.layout.ViwnLayout2;
import pl.edu.pwr.wordnetloom.ui.visualisation.listeners.GraphChangeListener;
import pl.edu.pwr.wordnetloom.ui.visualisation.listeners.SynsetSelectionChangeListener;
import pl.edu.pwr.wordnetloom.ui.visualisation.listeners.ViwnGraphMouseListener;
import pl.edu.pwr.wordnetloom.ui.visualisation.renderes.AstrideLabelRenderer;
import pl.edu.pwr.wordnetloom.ui.visualisation.renderes.ViwnVertexFillColor;
import pl.edu.pwr.wordnetloom.ui.visualisation.renderes.ViwnVertexRenderer;
import pl.edu.pwr.wordnetloom.ui.visualisation.structure.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.List;

public class ViwnGraphViewUI implements Loggable {

    private final DirectedGraph<ViwnNode, ViwnEdge> forest = new DirectedSparseMultigraph<>();

    private final HashMap<Long, ViwnNodeSynset> cache = new HashMap<>();

    private final ViwnLayout2 layout = new ViwnLayout2(forest);

    private VisualizationViewer<ViwnNode, ViwnEdge> vv = null;

    private ViwnNodeRoot rootNode = null;

    // Collection of object which listen for an event of synset selection
    // change.
    protected Collection<SynsetSelectionChangeListener> synsetSelectionChangeListeners = new ArrayList<>();

    // Collection of objects which listen for an event of visualisation changes
    protected Collection<GraphChangeListener> graphChangeListeners = new ArrayList<>();

    private ViwnNode selectedNode = null;

    private int openedFromTabIndex;

    private final ScalingControl scaler = new ViewScalingControl();

    // Graph mouse listener, handles mouse clicks at vertices
    private ViwnGraphMouseListener graphMouseListener = null;

    private static final float EDGE_PICK_SIZE = 10f;
    final int MAX_SYNSETS_SHOWN = 4;
    final int MIN_SYNSETS_IN_GROUP = 2;

    /* Transient cache for visualisation biulding */
    private HashMap<Long, DataEntry> entrySets = new HashMap<>();

    public void setEntrySets(HashMap<Long, DataEntry> entrySets) {
        this.entrySets = entrySets;
    }

    public DataEntry getEntrySetFor(Long id) {
        return entrySets.get(id); // if there is no cache returns null
    }

    public void addSynsetToCash(Long synsetId, ViwnNodeSynset node) {
        cache.put(synsetId, node);
    }

    public List<SynsetRelation> getRelationsFor(Long id) {
        DataEntry e = getEntrySetFor(id);
        if (e == null) {
            return new ArrayList<>();
        }

        ArrayList<SynsetRelation> rels = new ArrayList<>();
//        rels.addAll(e.getRelsFrom()); //TODO dorobić
//        rels.addAll(e.getRelsTo());

        return rels;
    }

    public Set<SynsetRelation> getUpperRelationsFor(Long id) {
        DataEntry e = getEntrySetFor(id);
        if (e == null) {
            return null;
        }

//        return e.getRelsFrom();

        return null; // TODO dorobić
    }

    public Set<SynsetRelation> getSubRelationsFor(Long id) {
        DataEntry e = getEntrySetFor(id);
        if (e == null) {
            return null;
        }

//        return e.getRelsTo();
        return null; //TODO dorobić
    }

    public void releaseDataSetCache() {
        entrySets.clear();
    }

    public JComponent getRootComponent() {
        return null;
    }


    public JPanel getSampleGraphViewer() throws IOException {
        vv = new VisualizationViewer<>(layout);

        vv.getRenderer().setVertexRenderer(
                new ViwnVertexRenderer(vv.getRenderer().getVertexRenderer()));
        HashMap<RenderingHints.Key, Object> hints = new HashMap<>();
        hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
        hints.put(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        hints.put(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
        hints.put(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        hints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        hints.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_SPEED);
        vv.setRenderingHints(hints);

        RenderContext<ViwnNode, ViwnEdge> rc = vv.getRenderContext();

        rc.setVertexShapeTransformer((ViwnNode v) -> v.getShape());

        rc.setVertexFillPaintTransformer(new ViwnVertexFillColor(vv
                .getPickedVertexState(), rootNode));

        rc.setEdgeLabelClosenessTransformer(new ConstantDirectionalEdgeValueTransformer<>(
                0.5, 0.5));

        rc.setEdgeIncludePredicate((Context<Graph<ViwnNode, ViwnEdge>, ViwnEdge> context) -> {
            if (context.element instanceof ViwnEdgeCandidate) {
                ViwnEdgeCandidate cand = (ViwnEdgeCandidate) context.element;
                return !cand.isHidden();
            }
            return true;
        });

        Transformer<ViwnEdge, Paint> edgeDrawColor = (ViwnEdge e) -> e.getColor();
        rc.setEdgeDrawPaintTransformer(edgeDrawColor);
        rc.setArrowDrawPaintTransformer(edgeDrawColor);
        rc.setArrowFillPaintTransformer(edgeDrawColor);

        rc.setEdgeStrokeTransformer(new ViwnEdgeStrokeTransformer());

        graphMouseListener = new ViwnGraphMouseListener(this);
        vv.addGraphMouseListener(graphMouseListener);

        Transformer<ViwnEdge, String> stringer = (ViwnEdge rel) -> rel.toString();
        rc.setEdgeLabelTransformer(stringer);
        rc.setEdgeShapeTransformer(new EdgeShape.Line<>());

        ViwnGraphViewModalGraphMouse gm = new ViwnGraphViewModalGraphMouse(this);
        vv.addKeyListener(gm.getModeKeyListener());

        vv.getRenderer().setEdgeLabelRenderer(new AstrideLabelRenderer<>());

        vv.setGraphMouse(gm);

        GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        panel.add(vv);

        ((ShapePickSupport<ViwnNode, ViwnEdge>) rc.getPickSupport())
                .setPickSize(EDGE_PICK_SIZE);

        vv.setVertexToolTipTransformer(new ViwnVertexToolTipTransformer());

        return panel;
    }

    /**
     * @return cache_
     */
    public HashMap<Long, ViwnNodeSynset> getCache() {
        return cache;
    }

    /**
     * clears cache
     */
    public void cleanCache() {
        cache.clear();
    }

    /**
     * @param synset
     */
    public void refreshView(Synset synset) {
        // Clear the visualisation.
        clear();

        selectedNode = rootNode = new ViwnNodeSynset(synset, this);
        ViwnNodeSynset rootSynsetNode = (ViwnNodeSynset) rootNode;
        cache.put(synset.getId(), rootSynsetNode);

        vv.getRenderContext().setVertexFillPaintTransformer(
                new ViwnVertexFillColor(vv.getPickedVertexState(), rootNode));

        if (!forest.containsVertex(rootNode)) {
            forest.addVertex(rootNode);
        }

        try {
            cache.values().stream().map((n) -> {
                n.setSpawner(null, null);
                return n;
            }).filter((n) -> (n instanceof ViwnNodeSynset)).forEachOrdered((n) -> {
                for (NodeDirection rclass : NodeDirection.values()) {
                    n.setState(rclass, ViwnNodeSynset.State.NOT_EXPANDED);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (NodeDirection dir : NodeDirection.values()) {
            if (rootNode instanceof ViwnNodeSynset) {
                ((ViwnNodeSynset) rootNode).setState(dir,
                        ViwnNodeSynset.State.EXPANDED);
            }
            if (dir != NodeDirection.IGNORE) {
                showRelationGUI(rootSynsetNode, dir);
            }
        }

        recreateLayout();
        center();
        vv.setVisible(true);
//        releaseDataSetCache();
    }

    /**
     * Recreate a visualisation layout. Currently fires graphChanged event, this should
     * be done somewhere else
     *
     * @author amusial
     */
    public void recreateLayout() {
        layout.mapNodes2Points(rootNode);

        graphChanged();
        /*
         * this event above (graphChanged) should be fired only when visualisation
		 * changes, and not in here where layout is recreated, it should be
		 * fired when new nodes shows or some nodes hides...
         */
    }

    /**
     * place selected node in the center of the screen
     *
     * @author amusial
     */
    public void center() {
        ViwnNode central;

        if (selectedNode != null) {
            central = selectedNode;
        } else if (rootNode != null) {
            central = rootNode;
        } else {
            return;
        }

        Point2D q = layout.transform(central);
        Point2D lvc = vv.getRenderContext().getMultiLayerTransformer()
                .inverseTransform(vv.getCenter());
        vv.getRenderContext().getMultiLayerTransformer()
                .getTransformer(Layer.LAYOUT)
                .translate(lvc.getX() - q.getX(), lvc.getY() - q.getY());

    }

    /**
     * deselects all visualisation nodes
     */
    public void deselectAll() {
        vv.getPickedVertexState().clear();
    }

    /**
     * scale visualisation to fill full visualization viewer space
     *
     * @author amusial
     */
    protected void fillVV() {
        // scale
        // if layout was scaled, scale it to it original size
        if (vv.getRenderContext().getMultiLayerTransformer()
                .getTransformer(Layer.LAYOUT).getScaleX() > 1D) {
            (new LayoutScalingControl()).scale(vv, (1f / (float) vv
                            .getRenderContext().getMultiLayerTransformer()
                            .getTransformer(Layer.LAYOUT).getScaleX()),
                    new Point2D.Double());
        }
        // get view bounds
        Dimension vd = vv.getPreferredSize();
        if (vv.isShowing()) {
            vd = vv.getSize();
        }
        // get visualisation layout size
        Dimension ld = vv.getGraphLayout().getSize();
        // finally scale it if view bounds are different than visualisation layer bounds
        if (vd.equals(ld) == false) {
            float heightRatio = (float) (vd.getWidth() / ld.getWidth());
            float widthRatio = (float) (vd
                    .getHeight() / ld.getHeight());

            scaler.scale(vv, (heightRatio < widthRatio ? heightRatio
                    : widthRatio), new Point2D.Double());
        }
    }

    /**
     * @param node
     * @param rel
     */
    public void checkState(ViwnNodeSynset node, NodeDirection rel) {
        node.setState(rel, ViwnNodeSynset.State.NOT_EXPANDED);
        boolean all_ok = true;

        for (ViwnEdgeSynset e : node.getRelation(rel)) {
            ViwnNodeSynset other = cache.get(e.getParent());
            if (other != null && other.equals(node)) {
                other = cache.get(e.getChild());
            }

            if (forest.getEdges().contains(e)) {
                node.setState(rel, ViwnNodeSynset.State.SEMI_EXPANDED);
            } else if (!node.getSynsetSet(rel).getSynsets().contains(other)) {
                all_ok = false;
            }
        }

        if (all_ok) {
            if (forest.containsVertex(node.getSynsetSet(rel))
                    || node.getSynsetSet(rel).getSynsets().isEmpty()) {
                node.setState(rel, ViwnNodeSynset.State.EXPANDED);
            }
        }
    }

    /**
     * @param node
     * @param dir
     */
    public void checkStateAllInRel(ViwnNodeSynset node, NodeDirection dir) {
        for (ViwnEdgeSynset e : node.getRelation(dir)) {
            ViwnNodeSynset test = cache.get(e.getParent());
            if (test != null && test.equals(node)) {
                test = cache.get(e.getChild());
            }
            if (test != null) {
                checkState(test, dir.getOpposite());
            }
        }
    }

    /**
     * @return true when synsets can be grouped
     */
    public boolean canGroupSynsets() {
        Set<ViwnNode> picked = vv.getPickedVertexState().getPicked();
        if (picked.size() < 1) {
            return false;
        } else if (picked.size() == 1
                && picked.iterator().next() instanceof ViwnNodeSynset) {
            ViwnNodeSynset syns = (ViwnNodeSynset) picked.iterator().next();
            if (syns.equals(rootNode)) {
                return false;
            }
            if (!forest.containsVertex(syns)) {
                return false;
            }
            ViwnNodeSet s = getSetFrom(syns);
            if (!forest.containsVertex(s)) {
                return false;
            }
        }

        boolean can_group = false;
        Iterator<ViwnNode> pick_itr = picked.iterator();
        if (pick_itr.hasNext()) {

            ViwnNode spawner = pick_itr.next().getSpawner();
            if (spawner != null) {
                while (pick_itr != null && pick_itr.hasNext()) {
                    if (spawner != pick_itr.next().getSpawner()) {
                        pick_itr = null;
                    }
                }
                if (pick_itr != null) {
                    can_group = true;
                }
            }
        }
        return can_group;
    }

    private ViwnEdgeSynset findRelation(ViwnNodeSynset s1, ViwnNodeSynset s2,
                                        NodeDirection rel) {
        for (ViwnEdgeSynset s : s1.getRelation(rel)) {
            if ((cache.get(s.getChild()) != null && cache.get(s.getChild())
                    .equals(s2))
                    || (cache.get(s.getParent()) != null && cache.get(
                    s.getParent()).equals(s2))) {
                return s;
            }
        }

        return null;
    }

    private void addEdge(ViwnEdge e, Long first, Long second) {
        ViwnNode v1 = cache.get(first);
        ViwnNode v2 = cache.get(second);
        if (v1 != null && v2 != null) {
            addEdge(e, (ViwnNodeSynset) v1, (ViwnNodeSynset) v2);
        }
    }

    private void addEdge(ViwnEdge e, ViwnNodeSynset first, ViwnNodeSynset second) {
        ViwnEdgeSynset se = (ViwnEdgeSynset) e;
        se.setSynset1(first);
        se.setSynset2(second);
        forest.addEdge(e, first, second);
    }

    private Collection<ViwnNodeSynset> addMissingRelations(ViwnNodeSynset synset) {
        ArrayList<ViwnNodeSynset> changed = new ArrayList<>();
        for (NodeDirection dir : NodeDirection.values()) {
            for (ViwnEdgeSynset e : synset.getRelation(dir)) {
                ViwnNodeSynset inner = null;
                if (!forest.containsEdge(e)) {
                    if (synset == cache.get(e.getChild())) {
                        inner = cache.get(e.getParent());
                    } else {
                        inner = cache.get(e.getChild());
                    }
                    if (forest.containsVertex(inner)) {
                        addEdge(e, e.getParent(), e.getChild());

                        for (NodeDirection in_dir : NodeDirection.values()) {
                            if (inner.getSynsetSet(in_dir).contains(synset)) {
                                inner.getSynsetSet(in_dir).remove(synset);
                            }
                        }
                        changed.add(inner);
                    }
                }
            }
        }
        return changed;
    }

    private ViwnNodeSet getSetFrom(ViwnNodeSynset synset) {
        ViwnNodeRoot spawner = (ViwnNodeRoot) synset.getSpawner();
        return spawner.getSynsetSet(synset.getSpawnDir());
    }

    private void addEdgeSynsSet(ViwnNodeRoot syns, ViwnNodeSet set,
                                NodeDirection dir) {
        ViwnEdgeSet e = new ViwnEdgeSet();
        switch (dir) {
            case TOP:
            case BOTTOM:
                forest.addEdge(e, syns, set);
                break;
            case LEFT:
            case RIGHT:
                forest.addEdge(e, set, syns);
                break;
        }
    }

    private void addSynsetFromSet_(ViwnNodeSynset synset) {
        synset.setSet(null);
        forest.addVertex(synset);

        ViwnNodeSynset spawner = (ViwnNodeSynset) synset.getSpawner();
        ViwnNodeSet set = getSetFrom(synset);

        set.remove(synset);

        ViwnEdgeSynset e = findRelation(spawner, synset, synset.getSpawnDir());

        addEdge(e, loadSynsetNode(e.getSynsetFrom()),
                loadSynsetNode(e.getSynsetTo()));

        if (set.getSynsets().isEmpty()) {
            forest.removeVertex(set);
        }

        Collection<ViwnNodeSynset> changed = new ArrayList<>();

        for (ViwnNode node : forest.getVertices()) {
            if (node instanceof ViwnNodeSynset) {
                changed.addAll(addMissingRelations((ViwnNodeSynset) node));
            }
        }

        {
            Iterator<ViwnNodeSynset> iter = changed.iterator();
            while (iter.hasNext()) {
                ViwnNodeSynset node = iter.next();
                for (NodeDirection rclass : NodeDirection.values()) {
                    checkState(node, rclass);
                }
            }
        }

        for (NodeDirection dir : NodeDirection.values()) {
            checkState(synset, dir);
        }
    }

    /**
     * @param synset
     */
    public void addSynsetFromSet(ViwnNodeSynset synset) {
        addSynsetFromSet_(synset);
        vv.getPickedVertexState().pick(synset, true);
        if (getSetFrom(synset).getSynsets().size() == 1) {
            ViwnNodeSynset last = getSetFrom(synset).getSynsets().iterator()
                    .next();
            addSynsetFromSet_(last);
            vv.getPickedVertexState().pick(last, true);
        }
        selectedNode = synset;

    }

    /**
     * @param synset
     * @return set
     */
    public ViwnNodeSet addSynsetToSet(ViwnNodeSynset synset) {

        for (NodeDirection dir : NodeDirection.values()) {
            hideRelation(synset, dir);
        }
        forest.removeVertex(synset);
        ViwnNodeSet set = getSetFrom(synset);
        set.add(synset);
        synset.setSet(set);

        // if this is first synset, add the set to the visualisation
        if (set.getSynsets().size() == 1) {
            forest.addVertex(set);
            addEdgeSynsSet((ViwnNodeSynset) synset.getSpawner(), set,
                    synset.getSpawnDir());
            set.setSpawner(synset.getSpawner(), synset.getSpawnDir());
        }
        selectedNode = set;

        checkAllStates();

        return set;
    }

    /**
     * @param synsetNode
     * @param hide_dir
     */
    public void hideRelation(ViwnNodeSynset synsetNode, NodeDirection hide_dir) {
        synchronized (forest) {
            boolean semi = false;
            boolean changed = false;
            for (ViwnEdgeSynset r : synsetNode.getRelation(hide_dir)) {
                ViwnNodeSynset rem = cache.get(r.getParent());
                if (rem != null && rem.equals(synsetNode)) {
                    rem = cache.get(r.getChild());
                }

                if (rem != null && forest.containsVertex(rem)) {
                    if (rem.getSpawner() != null
                            && rem.getSpawner().equals(synsetNode)) {
                        for (NodeDirection rel : NodeDirection.values()) {
                            hideRelation(rem, rel);
                        }
                        forest.removeEdge(r);
                        forest.removeVertex(rem);
                        changed = true;
                    } else {
                        semi = true;
                    }
                }
            }
            if (semi && changed) {
                synsetNode.setState(hide_dir,
                        ViwnNodeSynset.State.SEMI_EXPANDED);
            } else if (changed) {
                synsetNode.setState(hide_dir, ViwnNodeSynset.State.NOT_EXPANDED);
            }
            synsetNode.getSynsetSet(hide_dir).removeAll();
            forest.removeVertex(synsetNode.getSynsetSet(hide_dir));
            checkAllStates();
        }
    }

    private void checkAllStates() {
        for (ViwnNode node : forest.getVertices()) {
            if (node instanceof ViwnNodeSynset) {
                for (NodeDirection d : NodeDirection.values()) {
                    checkState((ViwnNodeSynset) node, d);
                }
            }
        }
    }

    public void showRelation(ViwnNodeSynset synsetNode,
                             NodeDirection[] dirs) {

        setSelectedNode(synsetNode);
        for (NodeDirection dir : dirs) {
            showRelationGUI(synsetNode, dir);
        }

        SwingUtilities.invokeLater(() -> {
            recreateLayoutWithFix(synsetNode, synsetNode);
            recreateLayout();
            vv.repaint();
        });
    }

    /**
     * @param synsetNode node which relations will be shown
     * @param dir        relation class which will be shown
     */
    private void showRelationGUI(ViwnNodeSynset synsetNode, NodeDirection dir) {
        List<ViwnEdgeSynset> relations = (List<ViwnEdgeSynset>) synsetNode.getRelation(dir); //TODO można to przerobić tak, aby było rozdzielenie na od i do
//        relations.sort(new Comparator<ViwnEdgeSynset>() {
//            @Override
//            public int compare(ViwnEdgeSynset o1, ViwnEdgeSynset o2) {
//                int compareResult = loadSynsetNode(o1.getSynsetTo()).getLabel().compareTo(loadSynsetNode(o2.getSynsetTo()).getLabel());
//                if(compareResult==0){
//                    compareResult = loadSynsetNode(o1.getSynsetFrom()).getLabel().compareTo(loadSynsetNode(o2.getSynsetFrom()).getLabel());
//                }
//                return compareResult;
//            }
//        }); //TODO to tylko tymczasowe rozwiązanie. Nalezy pozmieniać we wszystkich miejscach gdzie sa przechowywane relacje na liste, aby nie było trzeba ponownie sortować. Wystarczy pobrać dane z bazy
        int toShow = Math.min(MAX_SYNSETS_SHOWN, relations.size());
        // dodanie elementów, które będą wyświetlane
        for (int i = 0; i < toShow; i++) {
            ViwnEdgeSynset edge = relations.get(i);
            ViwnNodeSynset node = loadSynsetNode(edge.getSynsetFrom());
            if (node.equals(synsetNode)) {
                node = loadSynsetNode(edge.getSynsetTo());
            }
            if (!forest.containsVertex(node)) {
                if (node.getSet() != null) {
                    node.getSet().remove(node);
                    node.setSet(null);
                }
                //TOOD tutaj było coś ze sprawdzaniem etykiety
                node.setSpawner(synsetNode, dir);
                forest.addVertex(node);
            }
        }
        //dodanie elementów, które będą schowane
        if (relations.size() > MAX_SYNSETS_SHOWN) {

        }
        ViwnNodeSet set = synsetNode.getSynsetSet(dir);
        for (int i = toShow; i < relations.size(); i++) {
            ViwnEdgeSynset edge = relations.get(i); //TODO refaktor
            ViwnNodeSynset node = loadSynsetNode(edge.getSynsetFrom());
            if (node.equals(synsetNode)) {
                node = loadSynsetNode(edge.getSynsetTo());
            }
            if (!forest.containsVertex(node)) {
                if (set.contains(node)) {
                    continue;
                }
                node.setSpawner(synsetNode, dir);
                node.setSet(set);
                set.add(node);
            }
        }

        //TODO ogarnąć co się stanie, jeśli set będzie pusty
        if (!set.getSynsets().isEmpty()) {
            set.setSpawner(synsetNode, dir);
            forest.addVertex(set);
            addEdgeSynsSet(synsetNode, set, dir);
        }

        List<ViwnNode> nodes = new ArrayList<>(forest.getVertices());
        nodes.stream().filter((node) -> ((node instanceof ViwnNodeSynset))).forEachOrdered((node) -> addMissingRelations((ViwnNodeSynset) node));

        vv.setVisible(true);
        checkAllStates();
    }

    /**
     * Clear visualisation (removes all edges and nodes).
     */
    public void clear() {
        // Lock the visualisation object
        synchronized (forest) {

            new ArrayList<>(forest.getEdges()).forEach((o) -> {
                forest.removeEdge(o);
            });

            new ArrayList<>(forest.getVertices()).forEach((o) -> {
                forest.removeVertex(o);
            });
        }
    }

    public ViwnNodeSynset loadSynsetNode(Synset synset) {
        if (cache.containsKey(synset.getId())) {
            ViwnNodeSynset s = cache.get(synset.getId());
            if (s.isDirty()) {
                s.construct();
            }
            return s;
        }

        ViwnNodeSynset new_synset = new ViwnNodeSynset(synset, this);

        cache.put(synset.getId(), new_synset);
        return new_synset;
    }

    private NodeDirection findCommonRelationDir(ViwnNodeSynset parent,
                                                ViwnNodeSynset child) {
        for (NodeDirection dir : NodeDirection.values()) {
            for (ViwnEdgeSynset e : parent.getRelation(dir)) {
                if (e.getChild().equals(child.getSynset().getId())
                        || e.getParent().equals(child.getSynset().getId())) {
                    return dir;
                }
            }
        }
        return null;
    }

    public void addConnectedSynsetsToGraph(ViwnNodeSynset first, List<Synset> synsets) {
        ViwnNodeSynset prev = first;
        for (Synset synset : synsets) {
            ViwnNodeSynset node = loadSynsetNode(synset);
            relationAdded(prev, node);
            prev = node;
        }
    }

    public void relationAdded(ViwnNodeSynset from, ViwnNodeSynset to) {

        ViwnNodeSynset first = new ViwnNodeSynset(from.getSynset(), this);
        ViwnNodeSynset second = new ViwnNodeSynset(to.getSynset(), this);

        if (!forest.containsVertex(first)) {

            NodeDirection cdir = findCommonRelationDir(second, first);
            if (cdir != null) {
                first.setSpawner(second, cdir);
                forest.addVertex(first);
            }
            checkMissing();
            recreateLayoutWithFix(null, null);
        } else if (!forest.containsVertex(second)) {
            NodeDirection cdir = findCommonRelationDir(first, second);
            if (cdir != null) {
                second.setSpawner(first, cdir);
                forest.addVertex(second);
            }
            checkMissing();
            recreateLayoutWithFix(null, null);
        }
    }

    protected void checkMissing() {
        List<ViwnNode> nodes = new ArrayList<>(forest.getVertices());

        nodes.stream().filter((node) -> ((node instanceof ViwnNodeSynset)
                && !(node instanceof ViwnNodeCandExtension))).forEachOrdered((node) -> {
            addMissingRelations((ViwnNodeSynset) node);
        });

        nodes.stream().filter((node) -> (node instanceof ViwnNodeSynset)).forEachOrdered((node) -> {
            for (NodeDirection d : NodeDirection.values()) {
                checkState((ViwnNodeSynset) node, d);
            }
        });
    }

    private ViwnEdge getFirstOnPath(Graph<ViwnNode, ViwnEdge> g, ViwnNode v1,
                                    ViwnNode v2) {
        DijkstraShortestPath<ViwnNode, ViwnEdge> dsp = new DijkstraShortestPath<>(g);

        List<ViwnEdge> list = dsp.getPath(v1, v2);

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    private void setSpawnerByEdge(ViwnNodeSynset node, ViwnEdgeSynset edge) {

        ViwnNodeSynset second = cache.get(edge.getChild());
        if (second.equals(node)) {
            second = cache.get(edge.getParent());
        }
        if (second == null) {
            logger().error("node of edge not in cache");
        }

        NodeDirection d = findCommonRelationDir(second, node);
        if (d == null) {
            d = findCommonRelationDir(node, second);
            if (d != null) {
                d = d.getOpposite();
            }
        }

        if (d == null) {
            logger().error("can't find relation in any direction");
        }

        node.setSpawner(second, d);
    }

    private Graph<ViwnNode, ViwnEdge> undirect(Graph<ViwnNode, ViwnEdge> g) {
        Graph<ViwnNode, ViwnEdge> new_g = new UndirectedSparseGraph<>();
        forest.getVertices().stream().filter((n) -> (n instanceof ViwnNodeRoot)).forEachOrdered((n) -> {
            new_g.addVertex(n);
        });

        forest.getEdges().stream().filter((e) -> (e instanceof ViwnEdgeSynset || e instanceof ViwnEdgeCandidate)).forEachOrdered((e) -> {
            new_g.addEdge(e, forest.getEndpoints(e));
        });
        return new_g;
    }

    public void removeSynset(ViwnNodeSynset syns) {
        if (syns.equals(rootNode)) {
            clear();
            return;
        }

        for (NodeDirection dir : NodeDirection.values()) {
            forest.removeVertex(syns.getSynsetSet(dir));
            for (ViwnEdge ve : syns.getRelation(dir)) {
                ViwnEdgeSynset ves = (ViwnEdgeSynset) ve;
                if (!ves.getChild().equals(syns.getId())) {
                    ViwnNodeSynset n = cache.get(ves.getChild());
                    if (n != null) {
                        n.rereadDB();
                    }
                }

                if (!ves.getParent().equals(syns.getId())) {
                    ViwnNodeSynset n = cache.get(ves.getParent());
                    if (n != null) {
                        n.rereadDB();
                    }
                }
            }
        }

        relationDeleted(forest.getIncidentEdges(syns));

        forest.removeVertex(syns);
        cache.remove(syns.getId());
    }

    public void updateSynset(ViwnNodeSynset syns) {
        if (!forest.containsVertex(syns)) {
            return;
        }

        syns.rereadDB();

        addMissingRelations(syns);

        checkAllStates();
        recreateLayoutWithFix(syns, syns);
    }

    /**
     * @param edges
     */
    public void relationDeleted(Collection<ViwnEdge> edges) {
        if (edges == null) {
            return;
        }

        ViwnNode center = null;

        edges.forEach((col_e) -> {
            forest.removeEdge(col_e);
        });

        for (ViwnEdge col_e : edges) {
            ViwnEdgeSynset edge;
            if (col_e instanceof ViwnEdgeSynset) {
                edge = (ViwnEdgeSynset) col_e;
            } else {
                continue;
            }

            ViwnNodeSynset first = cache.get(edge.getChild());
            ViwnNodeSynset second = cache.get(edge.getParent());

            for (ViwnNodeSynset node : new ViwnNodeSynset[]{first, second}) {
                if (node == null) {
                    continue;
                }

                node.rereadDB();

                if (!forest.containsVertex(node)) {
                    continue;
                }

                if (!node.equals(rootNode)) {
                    if (forest.getIncidentEdges(node) == null) {
                        forest.removeVertex(node);
                    } else {
                        ViwnEdge e = getFirstOnPath(undirect(forest), node, rootNode);
                        if (e != null) {
                            center = node;
                            if (e instanceof ViwnEdgeCandidate) {
                                node.setSpawner(rootNode, NodeDirection.BOTTOM);
                            } else {
                                setSpawnerByEdge(node, (ViwnEdgeSynset) e);
                            }
                        } else if (node instanceof ViwnNodeCand) {
                            node.setSpawner(rootNode, NodeDirection.BOTTOM);
                            forest.addEdge(new ViwnEdgeCandidate(), rootNode, node);
                            center = node;
                        } else {
                            for (NodeDirection dir : NodeDirection.values()) {
                                hideRelation(node, dir);
                            }
                            forest.removeVertex(node);
                            if (first.equals(node)) {
                                center = second;
                            } else {
                                center = first;
                            }
                        }
                    }
                }
            }
        }

        checkAllStates();
        recreateLayoutWithFix(center, center);
    }


    /**
     * Invoke visualisation change events.
     *
     * @author amusial
     */
    public void graphChanged() {
        graphChangeListeners.forEach((gcl) -> {
            gcl.graphChanged();
        });
    }

    /**
     * selected node setter
     *
     * @param selected_node actually clicked
     */
    public void setSelectedNode(ViwnNode selected_node) {
        selectedNode = selected_node;
    }

    /**
     * @return visualization viewer
     */
    public VisualizationViewer<ViwnNode, ViwnEdge> getVisualizationViewer() {
        return vv;
    }

    /**
     * @return visualisation
     */
    public Graph<ViwnNode, ViwnEdge> getGraph() {
        return forest;
    }

    /**
     * @return layout
     */

    public Layout<ViwnNode, ViwnEdge> getLayout() {
        return layout;
    }

    /**
     * @return rootNode
     */
    public ViwnNode getRootNode() {
        return rootNode;
    }

    /**
     * @return selectedNode
     */
    public ViwnNode getSelectedNode() {
        return selectedNode;
    }

    /**
     * @param cursor new value of <code>VisualizationViewer</code> cursor
     * @author amusial
     */
    public void setCursor(Cursor cursor) {
        vv.setCursor(cursor);
    }

    /**
     * v1 and v2 could be same node, then it will stay in same point of view
     *
     * @param v1 node in first point of transformation vector
     * @param v2 node in second point of transformation vector
     * @author amusial
     */
    public void recreateLayoutWithFix(ViwnNode v1, ViwnNode v2) {
        /* remember location of group node */
        Point2D p1 = (Point2D) layout.transform(v1).clone();
        /* recreate layout */
        layout.mapNodes2Points(rootNode);
        Point2D p2 = (Point2D) layout.transform(v2).clone();
        /* transform layout */
        vv.getRenderContext().getMultiLayerTransformer()
                .getTransformer(Layer.LAYOUT)
                .translate(p1.getX() - p2.getX(), p1.getY() - p2.getY());
        /* fire visualisation changed event, do it somewhere else... */
        graphChanged();
    }

    public void saveToFile(String filename) {
        Dimension size = layout.getSize();

        VisualizationImageServer<ViwnNode, ViwnEdge> vv = new VisualizationImageServer<>(
                layout, size);

        vv.getRenderer().setVertexRenderer(
                new ViwnVertexRenderer(vv.getRenderer().getVertexRenderer()));

        RenderContext<ViwnNode, ViwnEdge> rc = vv.getRenderContext();

        rc.setVertexShapeTransformer((ViwnNode v) -> v.getShape());

        rc.setVertexFillPaintTransformer(new ViwnVertexFillColor(vv
                .getPickedVertexState(), rootNode));

        rc.setEdgeLabelClosenessTransformer(new ConstantDirectionalEdgeValueTransformer<>(
                0.5, 0.5));

        rc.setEdgeIncludePredicate((Context<Graph<ViwnNode, ViwnEdge>, ViwnEdge> context) -> {
            if (context.element instanceof ViwnEdgeCandidate) {
                ViwnEdgeCandidate cand = (ViwnEdgeCandidate) context.element;
                return !cand.isHidden();
            }
            return true;
        });

        Transformer<ViwnEdge, Paint> edgeDrawColor = (ViwnEdge e) -> e.getColor();

        rc.setEdgeDrawPaintTransformer(edgeDrawColor);
        rc.setArrowDrawPaintTransformer(edgeDrawColor);
        rc.setArrowFillPaintTransformer(edgeDrawColor);

        rc.setEdgeStrokeTransformer(new ViwnEdgeStrokeTransformer());

        Transformer<ViwnEdge, String> stringer = (ViwnEdge rel) -> rel.toString();

        rc.setEdgeLabelTransformer(stringer);
        rc.setEdgeShapeTransformer(new EdgeShape.Line<>());

        ViwnGraphViewModalGraphMouse gm = new ViwnGraphViewModalGraphMouse(this);
        vv.addKeyListener(gm.getModeKeyListener());

        vv.getRenderer().setEdgeLabelRenderer(
                new AstrideLabelRenderer<>());

        BufferedImage myImage = new BufferedImage(size.width, size.height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = myImage.createGraphics();

        Image img = vv.getImage(new Point2D.Float(0, 0), size);
        g2.drawImage(img, 0, 0, null);

        try (OutputStream out = new FileOutputStream(filename)) {
            ImageIO.write(myImage, "png", out);
        } catch (FileNotFoundException ex) {
            logger().error("File not found", ex);
        } catch (IOException ex) {
            logger().error("IO Error", ex);
        }

    }

    public void vertexSelectionChange(ViwnNode synset) {
        if (selectedNode != synset) {
            selectedNode = synset;
        }

/*        // TODO: check me : if we are in make relation mode
        ViWordNetService s = ((ViWordNetService) workbench
                .getService("pl.edu.pwr.wordnetloom.client.plugins.viwordnet.ViWordNetService"));
        if (s.isMakeRelationModeOn()) {
            s.makeRelation(synset);
            return;
        }

        if (s.isMergeSynsetsModeOn()) {
            s.mergeSynsets(synset);
            return;
        }*/

        synsetSelectionChangeListeners.forEach((l) -> {
            l.synsetSelectionChangeListener(synset);
        });
    }

}