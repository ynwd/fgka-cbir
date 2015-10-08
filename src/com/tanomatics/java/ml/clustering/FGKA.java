package com.tanomatics.java.ml.clustering;

import com.tanomatics.java.cbir.App;
import com.tanomatics.java.ml.clustering.fgka.ga.Chromosom;
import com.tanomatics.java.ml.clustering.fgka.ga.FitnessFunction;
import com.tanomatics.java.ml.clustering.fgka.ga.GeneToString;
import com.tanomatics.java.ml.clustering.fgka.ga.Population;
import com.tanomatics.java.ml.clustering.fgka.ga.operator.KMeans;
import com.tanomatics.java.ml.clustering.fgka.ga.operator.Mutation;
import com.tanomatics.java.ml.clustering.fgka.ga.operator.Selection;
import com.tanomatics.java.ml.core.Dataset;


/**
 * @author yanuwid
 * 
 */
public class FGKA implements IClusterer{

	private Dataset dataset;
	private int K;
	private int G;
	private int Z;
	private float mP;
	private int N;
	private Object[] dataTypes;
	private Chromosom solutions; 
	public static String log;
	private App app;
	
	public FGKA(Object parent, Dataset dataset, int k, int g, int z, float mP){
		this(dataset, k, g, z, mP);
		this.app = (App) parent;
	}
	
	public FGKA(Dataset dataset, int k, int g, int z, float mP){
		this.dataset = dataset;
		this.K = k;
		this.G = g;
		this.Z = z;
		this.mP = mP;
	}
	
	
	public void doClustering() {
		double minTWCV = Float.POSITIVE_INFINITY;
		double maxTWCV = 0;
		double[] TWCV = new  double[Z];
		//double[] e = new double[Z];
		Population population = new Population();
		GeneToString toString 	= new GeneToString();
		
		N = dataset.size();
		dataTypes 	= new Object[K];
		
		if(app != null)
		app.pd.setMaximum(G*Z);
		
		for(int k =0; k < K; k++){
			dataTypes[k] = k;
		}
		
		for(int z = 0; z < Z ; z++ ){
			population.addChromosom(new Chromosom(N,toString,dataTypes, dataset));
		}
		
		FitnessFunction fitnessFunction = new FitnessFunction(population);
		population.setFitnessFunction(fitnessFunction);
		
		Selection selector = new Selection();
		Mutation mutator = new Mutation(dataset);
		KMeans kmeans = new KMeans(dataset);
		
		Population tmpPopulation = (Population) population.clone();
		
		int c = 0;
		for(int i = 0; i < G; i++){
			for (int z=0; z < Z; z++){	
				TWCV[z] = tmpPopulation.getChromosom(z).getTWCV();
				// e[z] = tmpPopulation.getChromosom(z).getLegalityRatio();
				if(TWCV[z] < minTWCV){
					minTWCV = TWCV[z];
					solutions = tmpPopulation.getChromosom(z);
				}
				if(TWCV[z] > maxTWCV){
					maxTWCV = TWCV[z];
				}
				log = "generation="+i+ " twcv=" + (float)solutions.getTWCV();
				c++;
				if(app !=null){	
					app.setLoggingString(log);
					app.pd.setPbText(log);
					app.pd.setValue(c);
				}
			}
			
			selector.doSelection(tmpPopulation);
			mutator.doMutation(selector.getNewPopulation(), mP);
			kmeans.doClustering(mutator.getNewPopulation());
			tmpPopulation = kmeans.getNewPopulation();
		}
		
		fitnessFunction = null;
		selector = null;
		mutator = null;
		kmeans = null;
		tmpPopulation = null;
		TWCV = null;
		population = null;
		toString = null;
	}

	public int getGeneration(){
		return this.G;
	}
	
	public int getCounter(){
		return 0;
	}
	
	public float[] getCentroid(int index) {
		double[][] cTemp = new double[K][dataset.get(0).length];
		Centroids centroids = new Centroids(dataset,solutions,K);
		cTemp[index] = centroids.getCentroids()[index];
		
		float[] c = new float[cTemp[0].length];
		for(int i = 0; i < c.length; i++){
			c[i] = (float) cTemp[index][i];
		}
		
		cTemp = null;
		centroids = null;
		return c;
	}

	public int[] getResults() {
		int[] s = new int[solutions.size()];
		for(int i =0; i < s.length; i++){
			s[i] = solutions.getGen(i);
		}
		return s;
	}

	public int getResults(int index) {
		return solutions.getGen(index);
	}
	
	public Chromosom getSolutions(){
		return solutions;
	}
	
	public static void main(String args[]){

			// init dataset
			Dataset a_data = new Dataset();

			// cluster 0
			float[] a = {1,1,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1};
			float[] b = {1,2,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,2,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,2,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,2,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,2,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,2,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,2,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,2,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,2,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,2,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,2,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,2,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,2,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,2,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,2,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,2,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,2,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,2,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,2,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,2,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,2,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,2,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,2,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,1,2,0,0,0,1,0,0,1,2,4,0,0,0,0,0,0,1,2};
			float[] c = {2,1,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,1,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,1,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,1,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,1,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,1,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,1,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,1,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,1,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,1,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,1,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,1,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,1,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,1,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,1,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,1,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,1,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,1,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,1,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,1,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,1,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,1,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,1,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,1,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,1};
			float[] d = {2,2,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,2,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,2,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,2,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,2,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,2,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,2,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,2,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,2,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,2,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,2,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,2,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,2,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,2,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,2,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,2,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,2,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,2,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,2,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,2,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,2,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,2,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,2,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2,2,0,0,0,2,0,0,1,2,4,0,0,0,0,0,0,2,2};
			
			//cluster 1
			float[] e = {8,1,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,1,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,1,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,1,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,1,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,1,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,1,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,1,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,1,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,1,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,1,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,1,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,1,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,1,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,1,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,1,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,1,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,1,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,1,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,1,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,1,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,1,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,1,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,1,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,1};
			float[] f = {8,2,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,2,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,2,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,2,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,2,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,2,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,2,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,2,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,2,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,2,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,2,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,2,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,2,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,2,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,2,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,2,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,2,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,2,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,2,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,2,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,2,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,2,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,2,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,8,2,0,0,0,8,0,0,1,2,4,0,0,0,0,0,0,8,2};
			float[] g = {9,1,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,1,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,1,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,1,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,1,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,1,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,1,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,1,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,1,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,1,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,1,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,1,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,1,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,1,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,1,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,1,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,1,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,1,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,1,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,1,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,1,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,1,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,1,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,1,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,1};
			float[] h = {9,2,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,2,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,2,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,2,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,2,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,2,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,2,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,2,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,2,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,2,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,2,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,2,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,2,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,2,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,2,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,2,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,2,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,2,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,2,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,2,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,2,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,2,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,2,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,9,2,0,0,0,9,0,0,1,2,4,0,0,0,0,0,0,9,2};
			
			//cluster 2
			float[] i = {4,6,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,6,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,6,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,6,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,6,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,6,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,6,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,6,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,6,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,6,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,6,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,6,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,6,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,6,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,6,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,6,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,6,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,6,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,6,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,6,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,6,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,6,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,6,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,6,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,6};
			float[] j = {4,7,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,7,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,7,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,7,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,7,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,7,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,7,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,7,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,7,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,7,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,7,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,7,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,7,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,7,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,7,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,7,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,7,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,7,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,7,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,7,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,7,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,7,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,7,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,4,7,0,0,0,4,0,0,1,2,4,0,0,0,0,0,0,4,7};
			float[] k = {5,6,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,6,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,6,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,6,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,6,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,6,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,6,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,6,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,6,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,6,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,6,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,6,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,6,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,6,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,6,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,6,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,6,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,6,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,6,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,6,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,6,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,6,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,6,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,6,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,6};
			float[] l = {5,7,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,7,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,7,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,7,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,7,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,7,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,7,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,7,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,7,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,7,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,7,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,7,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,7,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,7,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,7,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,7,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,7,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,7,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,7,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,7,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,7,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,7,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,7,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,5,7,0,0,0,5,0,0,1,2,4,0,0,0,0,0,0,5,7};
			
			a_data.add(a);
			a_data.add(b);
			a_data.add(c);
			a_data.add(d);
			
			a_data.add(e);
			a_data.add(f);
			a_data.add(g);
			a_data.add(h);
			
			a_data.add(i);
			a_data.add(j);
			a_data.add(k);
			a_data.add(l);
			
			FGKA fgka = new FGKA(a_data,3,10,10,0.1f);
			fgka.doClustering();
			System.out.println();
			System.out.println("the best solution:"+fgka.solutions+" "+fgka.solutions.getCentroid());
	}

	



}