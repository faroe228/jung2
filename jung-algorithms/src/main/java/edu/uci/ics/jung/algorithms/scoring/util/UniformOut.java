/*
 * Created on Jul 7, 2007
 *
 * Copyright (c) 2007, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.algorithms.scoring.util;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;

public class UniformOut<V,E> implements Transformer<E, Double>
{
    private Graph<V, E> graph;
    
    public UniformOut(Graph<V, E> graph)
    {
        this.graph = graph;
    }
    
    public Double transform(E edge)
    {
        return 1.0 / graph.outDegree(graph.getSource(edge));
    }
}