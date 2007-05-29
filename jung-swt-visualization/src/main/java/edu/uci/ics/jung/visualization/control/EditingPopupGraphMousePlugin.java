package edu.uci.ics.jung.visualization.control;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.event.MouseEvent;
import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * a plugin that uses popup menus to create vertices, undirected edges,
 * and directed edges.
 * 
 * @author Tom Nelson
 *
 */
public class EditingPopupGraphMousePlugin<V,E> extends AbstractPopupGraphMousePlugin {
    
    protected Layout<V,E> layout;
    protected Factory<V> vertexFactory;
    protected Factory<E> edgeFactory;

    public EditingPopupGraphMousePlugin(Layout<V,E> layout,
    		Factory<V> vertexFactory, Factory<E> edgeFactory) {
        this.layout = layout;
        this.vertexFactory = vertexFactory;
        this.edgeFactory = edgeFactory;
    }

    public Layout<V, E> getLayout() {
		return layout;
	}

	public void setLayout(Layout<V, E> layout) {
		this.layout = layout;
	}

	@SuppressWarnings({ "unchecked", "serial", "serial" })
	protected void handlePopup(MouseEvent e) {
        final VisualizationViewer<V,E> vv =
            (VisualizationViewer<V,E>)e.getSource();
        final Layout<V,E> layout = vv.getGraphLayout();
        final Graph<V,E> graph = layout.getGraph();
        final Point2D p = e.getPoint();
        final Point2D ivp = p;//vv.getRenderContext().getBasicTransformer().inverseViewTransform(e.getPoint());
        GraphElementAccessor<V,E> pickSupport = vv.getServer().getPickSupport();
        if(pickSupport != null) {
            
            final V vertex = pickSupport.getVertex(layout, ivp.getX(), ivp.getY());
            final E edge = pickSupport.getEdge(layout, ivp.getX(), ivp.getY());
            final PickedState<V> pickedVertexState = vv.getServer().getPickedVertexState();
            final PickedState<E> pickedEdgeState = vv.getServer().getPickedEdgeState();
            JPopupMenu popup = new JPopupMenu();
            
            if(vertex != null) {
            	Set<V> picked = pickedVertexState.getPicked();
            	if(picked.size() > 0) {
            		if(graph instanceof UndirectedGraph == false) {
            			JMenu directedMenu = new JMenu("Create Directed Edge");
            			popup.add(directedMenu);
            			for(final V other : picked) {
//          				final Number other = iterator.next();
            				directedMenu.add(new AbstractAction("["+other+","+vertex+"]") {
            					public void actionPerformed(ActionEvent e) {
//          						Number newEdge = new Number(other, vertex);
            						graph.addEdge(edgeFactory.create(),
//          								graph.getEdgeCount(), 
            								other, vertex, EdgeType.DIRECTED);
            						vv.repaint();
            					}
            				});
            			}
            		}
            		if(graph instanceof DirectedGraph == false) {
            			JMenu undirectedMenu = new JMenu("Create Undirected Edge");
            			popup.add(undirectedMenu);
            			for(final V other : picked) {
            				undirectedMenu.add(new AbstractAction("[" + other+","+vertex+"]") {
            					public void actionPerformed(ActionEvent e) {
            						graph.addEdge(edgeFactory.create(),
            								//graph.getEdgeCount(), 
            								other, vertex);
            						vv.repaint();
            					}
            				});
            			}
            		}
                }
                popup.add(new AbstractAction("Delete Vertex") {
                    public void actionPerformed(ActionEvent e) {
                        pickedVertexState.pick(vertex, false);
                        graph.removeVertex(vertex);
                        vv.repaint();
                    }});
            } else if(edge != null) {
                popup.add(new AbstractAction("Delete Edge") {
                    public void actionPerformed(ActionEvent e) {
                        pickedEdgeState.pick(edge, false);
                        graph.removeEdge(edge);
                        vv.repaint();
                    }});
            } else {
                popup.add(new AbstractAction("Create Vertex") {
                    public void actionPerformed(ActionEvent e) {
                        V newVertex = vertexFactory.create();
                        layout.setLocation(newVertex, vv.getRenderContext().getMultiLayerTransformer().inverseTransform(p));
                        Layout layout = vv.getGraphLayout();
                        for(V vertex : graph.getVertices()) {
                            layout.lock(vertex, true);
                        }
                        graph.addVertex(vertexFactory.create());
//                        vv.getModel().restart();
                        for(V vertex : graph.getVertices()) {
                            layout.lock(vertex, false);
                        }
                        vv.repaint();
                    }
                });
            }
            if(popup.getComponentCount() > 0) {
                popup.show((Component)vv, e.getX(), e.getY());
            }
        }
    }
}
