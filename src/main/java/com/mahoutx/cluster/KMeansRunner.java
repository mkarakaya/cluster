/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahoutx.cluster;

import com.mahoutx.evaluate.Evaluator;
import com.mahoutx.data.DataCollector;
import com.mahoutx.model.GenCluster;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.mahout.clustering.DistanceMeasureCluster;
import org.apache.mahout.clustering.kmeans.Cluster;
import org.apache.mahout.clustering.kmeans.KMeansClusterer;
import org.apache.mahout.common.distance.DistanceMeasure;
import org.apache.mahout.math.Vector;

/**
 *
 * @author p.bell
 */
public class KMeansRunner extends Runner {

    public KMeansRunner(DistanceMeasure measure, DataCollector dataCollector) {
        super(measure, dataCollector);
    }

    @Override
    public void run() {

        try {
            List<Vector> sampleData = dataCollector.getData();
            FileWriter fileWriter = new FileWriter(new File(this.dataCollector.getClass().getSimpleName()
                    + this.measure.getClass().getSimpleName()
                    + KMeansClusterer.class.getSimpleName()));
            for (int i = 10; i <= 100; i += 10) {
                System.out.println("#ofClusters:" + i);
                Collections.shuffle(sampleData);
                List<Cluster> clusters = getClusters(sampleData, i, this.measure);
                List<List<Cluster>> finalClusters = KMeansClusterer.clusterPoints(sampleData,
                        clusters,
                        this.measure, 10, 0.01);
                fileWriter.append(i + ";");
                Evaluator evaluator = new Evaluator();
                List<GenCluster> genCluster =
                        convertToGenCluster(finalClusters.get(finalClusters.size() - 2));
                evaluator.evaluateCluster(genCluster,
                        this.measure, sampleData, fileWriter);
            }
            fileWriter.close();
        } catch (Exception e) {
            System.out.println("Error in KMeansRunner " + e.getMessage());
        }
    }

    private static List<Cluster> getClusters(List<Vector> sampleData, int numberOfClusters,
            DistanceMeasure distanceMeasure) {
        int clusterId = 0;
        List<Cluster> clusters = new ArrayList<Cluster>();
        for (Vector v : sampleData) {
            clusters.add(new org.apache.mahout.clustering.kmeans.Cluster(v, clusterId++,
                    distanceMeasure));
            if (clusterId == numberOfClusters) {
                break;
            }
        }
        return clusters;
    }

    private List<GenCluster> convertToGenCluster(List<Cluster> clusters) {
        List<GenCluster> genClusters = new ArrayList<GenCluster>();
        for (Cluster cluster : clusters) {
            GenCluster genCluster = new GenCluster(cluster.getId(),cluster.getCenter());
            genClusters.add(genCluster);
        }
        return genClusters;
    }
}
