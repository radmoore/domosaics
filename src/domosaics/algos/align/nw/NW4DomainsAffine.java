/* 
 * Implementation of some algorithms for pairwise alignment from
 * Durbin et al: Biological Sequence Analysis, CUP 1998, chapter 2.
 * Peter Sestoft, sestoft@itu.dk 1999-09-25, 2003-04-20 version 1.4
 * Reference:  http://www.itu.dk/people/sestoft/bsa.html
 *
 * License: Anybody can use this code for any purpose, including
 * teaching, research, and commercial purposes, provided proper
 * reference is made to its origin.  Neither the author nor the Royal
 * Veterinary and Agricultural University, Copenhagen, Denmark, can
 * take any responsibility for the consequences of using this code.
 */
package domosaics.algos.align.nw;

import domosaics.model.arrangement.Domain;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.arrangement.DomainVector;
import domosaics.model.arrangement.GapDomain;

/**
 * The "implementation of some algorithms for pairwise alignment from
 * Durbin et al: Biological Sequence Analysis, CUP 1998, chapter 2.
 * Peter Sestoft, sestoft@itu.dk 1999-09-25, 2003-04-20 version 1.4
 * Reference:  http://www.itu.dk/people/sestoft/bsa.html" was changed 
 * into a global alignment algorithm using Needleman-Wunsch 
 * (affine gap costs) for domains.  
 * <p>
 * The parameters are equivalent to the parameters used by Bjoerklund
 * et al. in their domain distance measure. Scores for <br>
 * match = 1000 (Bjoerklund used 1)<br>
 * missmatch = 0 (Bjoerklund used 0)<br>
 * gap opening = -10 (Bjoerklund used -0.01)<br>
 * gap extension = -1 (Bjoerklund used -0.001)<br>
 * <p>
 * When using the constructor the parameters are initialized and the
 * alignment process started. To get the results in form of a 
 * 2D DomainVector array use the method getMatch().
 * 
 * @author modified by Andreas Held
 *
 */
public class NW4DomainsAffine {
	
	/** score for a match, Bjoerklund used 1 */
	public static final int MATCH = 200;
	
	/** score for a missmatch, Bjoerklund used 0 */
	public static final int MISMATCH = -100;
	
	/** score for a gap opening, Bjoerklund used -0.01 */
	public static final int GAPOPENING = -100;
	
	/** score for a gap extension, Bjoerklund used -0.001 */
	public static final int GAPEXTENSION = -50;
	
	/** the input domain architectures */
	protected DomainArrangement da1, da2;	
	
	/** the corresponding domain vectors for the architecture */
	protected DomainVector doms1, doms2;	
	
	/** the alignment domain vectors which will hold the result including gap domains */
	protected DomainVector alignment1, alignment2;
	
	/** length of the domain vectors */
	protected int N, M;		
	
	/** Gap opening costs */
	protected int d;
	
	/** gap extension cost */
	protected int e;
	
	/** the starting point of the traceback */
	protected Traceback B0;    
	
	/**  the matrices used to compute the alignment */
	protected int[][][] F;        
	
	/**  the traceback matrix */
	protected Traceback3[][][] B;	
	
	/** negative infinity */
	protected final static int NegInf = Integer.MIN_VALUE/2;
	
	
	/**
	 * Constructor for a new DomainAlignment process which initializes 
	 * the needed parameters and starts the alignment process.
	 * 
	 * @param da1
	 * 		the first arrangement to be aligned
	 * @param da2
	 * 		the second arrangement to be aligned
	 */
	public NW4DomainsAffine(DomainArrangement da1, DomainArrangement da2) {
		d = GAPOPENING;
		e = GAPEXTENSION;
		
		this.da1 = da1;
		this.da2 = da2;
		
		doms1 = new DomainVector();
		doms1.add(da1.getDomains());
		
		doms2 = new DomainVector();
		doms2.add(da2.getDomains());
		
		N = this.doms1.size(); 
		M = this.doms2.size();

		alignment1 = new DomainVector();
		alignment2 = new DomainVector();
		
		F = new int[3][N+1][M+1];
		B = new Traceback3[3][N+1][M+1];
		
		align();
	}
	
	/**
	 * This method triggers the actual computation of the 
	 * alignment using NW with affine gap costs.
	 */
	public void align() {
		int n = N;
		int m = M;
		
		int[][] M = F[0], Ix = F[1], Iy = F[2];
		for (int i=1; i<=n; i++) {
			Ix[i][0] = -d - e * (i-1);
			B[1][i][0] = new Traceback3(1, i-1, 0);
		}
		for (int i=1; i<=n; i++)
			Iy[i][0] = M[i][0] = NegInf;
		for (int j=1; j<=m; j++) {
			Iy[0][j] = -d - e * (j-1);
			B[2][0][j] = new Traceback3(2, 0, j-1);
		}
		for (int j=1; j<=m; j++)
			Ix[0][j] = M[0][j] = NegInf;
		for (int i=1; i<=n; i++)
			for (int j=1; j<=m; j++) {
				int val;
				int s = score(doms1.get(i-1), doms2.get(j-1));
				val = M[i][j] = max(M[i-1][j-1]+s, Ix[i-1][j-1]+s, Iy[i-1][j-1]+s);
				if (val == M[i-1][j-1]+s)
					B[0][i][j] = new Traceback3(0, i-1, j-1);
				else if (val == Ix[i-1][j-1]+s)
					B[0][i][j] = new Traceback3(1, i-1, j-1);
				else if (val == Iy[i-1][j-1]+s)
					B[0][i][j] = new Traceback3(2, i-1, j-1);
				else 
					throw new Error("NWAffine 1");
				val = Ix[i][j] = max(M[i-1][j]-d, Ix[i-1][j]-e, Iy[i-1][j]-d);
				if (val == M[i-1][j]-d)
					B[1][i][j] = new Traceback3(0, i-1, j);
				else if (val == Ix[i-1][j]-e)
					B[1][i][j] = new Traceback3(1, i-1, j);
				else if (val == Iy[i-1][j]-d)
					B[1][i][j] = new Traceback3(2, i-1, j);
				else 
					throw new Error("NWAffine 2");
				val = Iy[i][j] = max(M[i][j-1]-d, Iy[i][j-1]-e, Ix[i][j-1]-d);
				if (val == M[i][j-1]-d)
					B[2][i][j] = new Traceback3(0, i, j-1);
				else if (val == Iy[i][j-1]-e)
					B[2][i][j] = new Traceback3(2, i, j-1);
				else if (val == Ix[i][j-1]-d)
					B[2][i][j] = new Traceback3(1, i, j-1);
				else 
					throw new Error("NWAffine 3");
			}
		
		// Find maximal score
		int maxk = 0;
		int maxval = F[0][n][m];
		for (int k=1; k<3; k++)
			if (maxval < F[k][n][m]) {
				maxval = F[k][n][m];
				maxk = k;
			}
		B0 = new Traceback3(maxk, n, m);
	}
	
	/**
	 * Helper method to calculate if the two domains are 
	 * a match or a missmatch.
	 * 
	 * @param dom1
	 * 		first domain to compare
	 * @param dom2
	 * 		second domain to compare
	 * @return
	 * 		whether or not the domain families are equal
	 */
	protected int score(Domain dom1, Domain dom2) {
		if (dom1.getFamily().equals(dom2.getFamily()))
			return MATCH;
		else
			return MISMATCH;
	}

	/**
	 * Return two-element array containing an alignment with maximal score.
	 * 
	 * @return
	 * 		2d alignment array containing the aligned architectures
	 */
	public DomainVector[] getMatch() {
		Traceback tb = B0;
		
		int i = tb.i;
		int j = tb.j;
		
		while ((tb = next(tb)) != null) {
			if (i == tb.i) 
				alignment1.add(0, new GapDomain(da1));
			else
				alignment1.add(0, doms1.get(i-1)); 
			if (j == tb.j) 
				alignment2.add(0, new GapDomain(da2));
			else
				alignment2.add(0, doms2.get(j-1)); 
			i = tb.i; 
			j = tb.j;
		}    
		
		DomainVector[] res = { alignment1, alignment2 };
		return res;
	}
	
	/**
	 * Returns the next traceback entry within the trace back matrix.
	 * 
	 * @param tb
	 * 		previous traceback field
	 * @return
	 * 		next traceback field
	 */
	public Traceback next(Traceback tb) {
		Traceback3 tb3 = (Traceback3)tb;
		return B[tb3.k][tb3.i][tb3.j];
	}

	/** 
	 * Returns the score for this alignment
	 * 
	 * @return
	 * 		score of this alignment
	 */
	public int getScore() { 
		return F[((Traceback3)B0).k][B0.i][B0.j]; 
	}

	static int max(int x1, int x2) { 
		return (x1 > x2 ? x1 : x2); 
	}

	static int max(int x1, int x2, int x3) { 
		return max(x1, max(x2, x3)); 
	}

	static int max(int x1, int x2, int x3, int x4) { 
		return max(max(x1, x2), max(x3, x4)); 
	}
	
}

abstract class Traceback {
	int i, j;                     // absolute coordinates
}

//Traceback3 objects for affine gap costs
class Traceback3 extends Traceback {
	int k;

	public Traceback3(int k, int i, int j) { 
		this.k = k; 
		this.i = i; 
		this.j = j; 
	}
}



