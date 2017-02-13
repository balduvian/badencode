package badencode;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Compress {

	BufferedImage[] patterns;
	BufferedImage disp;
	//String path = "C:\\Users\\ecoughlin7190\\Desktop\\colorful.jpg";
	String path = "C:\\Users\\Emmett\\Desktop\\source - texture\\compress\\treeriver.jpg";
	String opath;
	int[][][] encode;
	int[][][] git;
	final int uni = 8;
	
	public String[] bases(String s){
		String[] l = new String[3];
		int[] mark = new int[l.length];
		String temp = "";
		for(int i=s.length()-1;i >= 0;i--){
			char u = s.charAt(i);
			temp += u;
			if(u=='.'){
				mark[1] = i;
				l[2] = rev(temp);
				temp = "";
			}
			if(u=='\\'){
				mark[0] = i;
				temp = "";
				break;
			}
		}
		for(int i=0;i<s.length();i++){
			char u = s.charAt(i);
			temp += u;
			if(i==mark[0]){
				l[0] = temp;
				temp = "";
			}
			if(i==mark[1]-1){
				l[1] = temp;
				break;
			}
		}
		return l;
	}
	
	public String rev(String s){
		String temp = "";
		for(int i=s.length()-1;i >= 0;i--){
			temp += s.charAt(i);
		}
		return temp;
	}
	
	public String tolen(String s,int des){//makes binary fit nicely
		int upto = s.length();
		for(int i=0;i<des-upto;i++){//FIXXED
			s = "0"+s;
		}
		/*for(int i=0;i<upto-des;i++){
			String ret = "";
			for(int u=0;u<upto- (upto - upto-(upto-des))*-1;u++){
				ret += s.charAt(u);
			}
			s = ret;
		}*/
		return s;
	}
	
	public String doubin(int v, int l){//from int to x.x bytes...
		return tolen(Integer.toBinaryString(v),l);
	}
	
	public String twelvebitrgb(int[] c){//ayy lmao
		String r = tolen(Integer.toBinaryString((int)Math.round((c[0]/255.0)*15)),4);
		String g = tolen(Integer.toBinaryString((int)Math.round((c[1]/255.0)*15)),4);
		String b = tolen(Integer.toBinaryString((int)Math.round((c[2]/255.0)*15)),4);
		return r+g+b;
	}
	
	public int compcolor(Color c){//from color to byte
		String r = tolen(Integer.toBinaryString((int)Math.round((c.getRed()/255.0)*7)),3);
		String g = tolen(Integer.toBinaryString((int)Math.round((c.getGreen()/255.0)*7)),3);
		String b = tolen(Integer.toBinaryString((int)Math.round((c.getBlue()/255.0)*3)),2);
		return todec(r+g+b);
	}
	
	public int todec(String bin){//from binary to decimal
		int t=0;
		int ll = bin.length();
		for(int i=0;i<ll;i++){
			t += Character.getNumericValue(bin.charAt(i))*Math.pow(2, (ll-i-1));
		}
		return t;
	}
	
	public int todec(boolean[] bin){//from binary to decimal (but better)
		int t = 0;
		int ll = bin.length;
		for(int i=0;i<ll;i++){
			if(bin[i]){
				t += Math.pow(2, (ll-i-1));
			}
		}
		return t;
	}
	
	public int twelvecon(boolean[] c){//from twelve bit rgb to 24 bit rgb
		String base = "";
		for(int i=0;i<3;i++){
			int sta = todec(new boolean[]{c[i*4],c[i*4+1],c[i*4+2],c[i*4+3]});//staging
			sta = (int)(((double)sta/15)*255);
			base += doubin(sta,8);
		}
		return todec(base);
	}
	
	public Color decon(int c){//from byte to color
		String rgb = tolen(Integer.toBinaryString(c),8);
		String[] csp = new String[3];
		int[] csx = new int[3];
		for(int i=0;i<csp.length;i++){
			csp[i] = "";
		}
		for(int i=0;i<rgb.length();i++){
			if(i<3){
				csp[0] += rgb.charAt(i);
			}else if(i<6){
				csp[1] += rgb.charAt(i);
			}else{
				csp[2] += rgb.charAt(i);
			}
		}
		for(int i=0;i<csx.length;i++){
			csx[i] = todec(csp[i]);
		}
		return new Color((int)(csx[0]*(255.0/7)),(int)(csx[1]*(255.0/7)),(int)(csx[2]*(255.0/3)));
	}
	
	public boolean ditherand(int seed){
		if(Math.random()>0.5){
			return true;
		}
		return false;
		/*if((seed*10001.56)%2>0.5){
			return true;
		}
		return false;*/
	}
	
	int mark;//index pointer for dong
	boolean[] dong;
	public BufferedImage readtri(String url){//READ TRI
		
		boolean dither = true;
		
		File f = new File(url);
		try{
			FileInputStream o = new FileInputStream(f);
			int rem = o.available()+1;
			dong = new boolean[rem*8];
			for(int i=0;i<rem;i++){
				String tmp = doubin(o.read(),8);
				for(int u=0;u<8;u++){
					if(tmp.charAt(u)=='1'){
						dong[i*8+u] = true;
					}
				}
			}
			o.close();
		}catch(Exception ex){}
		
		mark = 0;//reset mark
		int ylim = getv(16);
		int xlim = getv(16);
		int uuni = getv(3);
		int logfinc = getv(3);
		int tmod = getv(2);
		int lmod =  getv(2);
		
		uuni = (int)Math.pow(2,uuni);
		int finc = (int)Math.pow(2,logfinc);
		tmod = (int)Math.pow(2,tmod);
		lmod = (int)Math.pow(2,lmod);
		
		int t = uuni/tmod;//height of pixel
		int l = uuni/lmod;//width of pixel
		int ppc = uuni*4; // actual pixels per chuck (32)
		int chl = (ppc)/t;//height of chunk in sim pixels
		int cwl = (ppc)/l;//width of chunk in sim pixels
		int h = (int)Math.ceil((double)ylim/t);//height of image in sim pixels
		int w = (int)Math.ceil((double)xlim/l);//width of image in sim pixels
		int ch = (int)Math.ceil((double)h/chl);//height of image in chunks
		int cw = (int)Math.ceil((double)w/cwl);//width of image in chunks 
		
		BufferedImage b = new BufferedImage(xlim,ylim,BufferedImage.TYPE_INT_RGB);
		for(int cy=0;cy<ch;cy++){
			for(int cx=0;cx<cw;cx++){
				try{
					int[] tempc = new int[finc];
					int numc = getv(logfinc)+1;
					for(int i=0;i<numc;i++){
						tempc[i] = twelvecon(getb(12));
					}
					int newlog = (int)Math.ceil(Math.log(numc)/Math.log(2));
					int lll = getv(6)+1;
					int www = getv(6)+1;
					for(int ty=0;ty<lll;ty++){
						for(int tx=0;tx<www;tx++){
							int cin = tempc[getv(newlog)];
							for(int y=-1;y<t+1;y++){
								for(int x=-1;x<l+1;x++){
									try{
										if(dither && (y==-1 || y==t+1 || x==-1 || x==l+1)){
											if(ditherand((cx*ppc+tx*l+x)*15 + (cy*ppc+ty*t+y)*15)){
												b.setRGB(cx*ppc+tx*l+x, cy*ppc+ty*t+y, cin);
											}
										}else{
											b.setRGB(cx*ppc+tx*l+x, cy*ppc+ty*t+y, cin);
										}
									}catch(Exception ex){}
								}
							}
						}
					}
				}catch(Exception ex){}
			}
		}
		dong = null;
		return b;
	}
	
	public boolean[] getb(int bits){
		boolean[] blo = new boolean[bits];
		for(int i=0;i<bits;i++){
			blo[i] = dong[mark];
			mark++;//advance the index
		}
		return blo;
	}
	
	public int getv(int bits){
		boolean[] blo = new boolean[bits];
		for(int i=0;i<bits;i++){
			blo[i] = dong[mark];
			mark++;//advance the index
		}
		return todec(blo);
	}
	
	public void writetri(String bin){
		String[] ba = bases(path);
		int pg = 0;
		File f = null;
		while(true){//find file name
			f = new File(ba[0]+ba[1]+pg+".tri");
			if(!f.exists()){
				break;
			}
			pg++;
		}
		try{
			FileOutputStream o = new FileOutputStream(f);
			int tot = (int)Math.ceil((double)bin.length()/8);
			for(int i=0;i<tot;i++){
				String temp = "";
				for(int u=0;u<8;u++){
					temp += bin.charAt(i*8+u)+"";
				}
				int wrn = todec(temp);
				o.write(wrn);
			}
			o.close();
		}catch(Exception ex){}
	}
	
	public class Wind extends JFrame{//WWWWWWWWWWIIIIIIIIIIINNNNNNNNNNNDDDDDDDDDDDDOOOOOOOOOOOOWWWWWWWWWWWWWWWW
		private static final long serialVersionUID = 2115409531656738321L;
		Can can;
		public Wind(){
			setTitle(".TRI Viewer");
			can = new Can();
			add(can);
			pack();
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			setVisible(true);
		}
		
		public class Can extends JPanel{
			public Can(){
				setPreferredSize(new Dimension(disp.getWidth(),disp.getHeight()));
			}
			private static final long serialVersionUID = 6662411703884413448L;
			public void paintComponent(Graphics g){
				super.paintComponent(g);
				try{
					g.drawImage(disp, 0, 0, null);
				}catch(Exception ex){}
				repaint();
			}
		}
	}
	
	public int sim(int[] a, int[] b){//returns how similar colors are smaller number, more similar;
		return Math.abs(a[0]-b[0]) + Math.abs(a[1]-b[1]) + Math.abs(a[2]-b[2]);
	}
	
	public int[] comb(int[] a,int[] b){//returns an average of two colors;;; ; ;
		return new int[]{(a[0]+b[0])/2,(a[1]+b[1])/2,(a[2]+b[2])/2};
	}
	
	public boolean cocomp(int[] a){
		for(int i=0;i<3;i++){
			if(a[i]!=-1){
				return false;
			}
		}
		return true;
	}
	
	public int rgbcomp(int[] a, int[] b){
		int stab = 0;
		for(int i=0;i<3;i++){
			stab += Math.abs(a[i]-b[i]);
		}
		return stab;
	}
	
	public void fourtwo(BufferedImage b){ //make bufferedimage for resampling
		int xlim = b.getWidth();
		int ylim = b.getHeight();
		
		int binfinc = (int)Math.ceil(Math.log(16)/Math.log(2));//how many bits to store for amount of colors
		int binuni = (int)Math.ceil(Math.log(uni)/Math.log(2));//bit compression of uni
		int btmod = (int)Math.ceil(Math.log(4)/Math.log(2));//you know how it goes
		int blmod = (int)Math.ceil(Math.log(4)/Math.log(2));
		
		int tmod = (int)Math.pow(2, btmod);//get the values of these variables from the logs   ^
		int lmod = (int)Math.pow(2, blmod);
		int finc = (int)Math.pow(2, binfinc);
		int uuni = (int)Math.pow(2, binuni);
				
		int t = uuni/tmod;//height of pixel
		int l = uuni/lmod;//width of pixel
		int ppc = uuni*4; // actual pixels per chuck (32 for uni 8)
		int chl = (ppc)/t;//height of chunk in sim pixels
		int cwl = (ppc)/l;//width of chunk in sim pixels
		int h = (int)Math.ceil((double)ylim/t);//height of image in sim pixels
		int w = (int)Math.ceil((double)xlim/l);//width of image in sim pixels
		int ch = (int)Math.ceil((double)h/chl);//height of image in chunks
		int cw = (int)Math.ceil((double)w/cwl);//width of image in chunks
		
		String binary = "";//OH BOI HERE WE GO
		
		binary += doubin(ylim,16);
		binary += doubin(xlim,16);
		binary += doubin(binuni,3);//similarly stored to finc  V
		binary += doubin(binfinc,3);//store a value of how many colors per chunk, working in powers of two from 1 to 256 colors.
		binary += doubin(btmod,2);//ok they're all stored like this
		binary += doubin(blmod,2);
		
		for(int cy=0;cy<ch;cy++){//for each chunk
			for(int cx=0;cx<cw;cx++){
				
				//OK FIRST COLLECT THE COLORS
				int[][] fell = new int[chl*cwl][3];//collector for colors
				int[][][] fdata = new int[chl][cwl][3];//storing the original colors in space 
				int felad = 0;//advance for fell
				int felc = 0;//counter for colors array
				
				int ybound = chl;//bounds for what's actually in the chunk (pixel space)
				int xbound = cwl;//starts at expected bounds
				boolean ybf = false;//booleans for if bounds are found yet
				boolean xbf = false;
				
				for(int ty=0;ty<chl;ty++){//for sim pixels in chunk
					for(int tx=0;tx<cwl;tx++){
						int[] gw = new int[3];
						int ti=0;
						for(int y=0;y<t;y++){
							for(int x=0;x<l;x++){
								try{
									Color c = new Color(b.getRGB(cx*ppc+tx*l+x, cy*ppc+ty*t+y));
									gw[0] += c.getRed();
									gw[1] += c.getGreen();
									gw[2] += c.getBlue();
									ti++;
								}catch(Exception ex){
								}
							}
						}
						if(!ybf && cy*ppc+ty*t>ylim-1){//if the whole pixel is off image, found ybound
							ybound = ty;
							ybf = true;
						}
						if(!xbf && cx*ppc+tx*l>xlim-1){//same for x
							xbound = tx;
							xbf = true;
						}
						if(xbf && ybf){//if both found, quit
							break;
						}
						
						try{//trycatch for divide by ti.. 0 means that pixel is off bounds
							int[] gal = {gw[0]/ti,gw[1]/ti,gw[2]/ti};//get the average
							fell[felad] = gal;//save it
							fdata[ty][tx] = gal;//save it in space
							felad++;
							felc++;//advance if try is passed so far
						}catch(Exception ex){}
					}
				}

				int[][] down = new int[felc][3];//the successor to fell
				for(int i=0;i<felc;i++){
					down[i] = fell[i];//copy it
				}
				
				//OK NOW REDUCE THE SAMPLE TO 8 COLORS
				int colors = cwl*chl; //amount of colors it can find (all it finds)
				
				int[][] div = new int[colors][3]; //array of temp colors (stored in RGB)
				for(int i=0;i<colors;i++){
					div[i] = new int[]{-1,-1,-1};//need this to protect the color black
				}
				int[][] fnal = new int[finc][3];//the final destination
				
				int[][] fit = down.clone();//that was a lot easier than color collector     <-------- new system
				
				//TIME FOR COMPRESSING
				int lvl = finc;//level of compression - starting at final color amount 
				int[][] initial = fit.clone();//setup initial
				int[][] allowed = new int[colors][3];//setup allowed
				int[][] forbidden = new int[colors][3];//forbidden (opposite of allowed)
				superloop: while(true){//it's super
					int pass = 0;//how many pass
					boolean[][][] space = new boolean[lvl][lvl][lvl];//colorspace increasingly getting smaller
					int alloc = 0;//index for where allowed next should go
					int falloc = 0;//indx for where forbidd next should go
					
					int ll = initial.length;
					int[][] comp = new int[ll][3];//setup compressed array (compressed version of initial)
					for(int i=0;i<ll;i++){
						for(int u=0;u<3;u++){
							comp[i][u] = (int)Math.round(((double)initial[i][u]/255)*(lvl-1));//compress
						}
						if(!space[comp[i][0]][comp[i][1]][comp[i][2]]){
							allowed[alloc] = initial[i];//transfer from initial into allowed
							alloc++;//advance lever
							space[comp[i][0]][comp[i][1]][comp[i][2]] = true;//fill up space
							pass++;//one passed!
						}else{//ONTO FORBIDDEN TERRITORY
							forbidden[falloc] = initial[i];//forbid
							falloc++;//advance lever
						}
					}
					
					//do some preparations
					int opass = pass;//an undisturbed pass;
					int tne = finc-pass;//how many times temp needs to be refilled
					if(pass<finc){//first to set up temp length to not be too short (8);
						pass = finc;
					}
					int[][] temp = new int[pass][3];
					for(int i=0;i<pass;i++){
						temp[i] = allowed[i];
					}
					for(int i=0;i<tne;i++){//for how many it needs to fill
						try{
							temp[opass+i] = forbidden[i];//fill at that loc with a previously forbidden color
						}catch(Exception ex){
							temp[opass+i] = new int[]{0,0,0};
						}
					}
					initial = temp.clone();//get all ready in initial for next round
					allowed = new int[pass][3];//reset allowed to it's new size, prep for next time around
					lvl--;//decrease level
					if(pass<=finc){//is it done???
						for(int i=0;i<pass;i++){//write to final
							fnal[i] = initial[i];//intial would have all that i need (from setup above)
						}
						break superloop;//get outta here
					}
					
				}
				
				boolean[] used = new boolean[finc];//what colors are being used
				int cused = 0;//how many colors used
				int[][] liberty = new int[ybound][xbound];//storing the colors before writing
				
				//second runthrough to get a feel for colors			
				for(int ty=0;ty<ybound;ty++){//for sim pixels in chunk //not chl and cwl
					for(int tx=0;tx<xbound;tx++){//smarter this time! (with bounds)
						
						int[] best = new int[2];//what, by how much
						best[1] = 999;//because it goes down... sigh*
						for(int i=0;i<finc;i++){
							int ny = rgbcomp(fdata[ty][tx],fnal[i]);
							if(ny<best[1]){
								best[1] = ny;
								best[0] = i;
							}
						}
						if(!used[best[0]]){
							used[best[0]] = true;//fill up space
							cused++;//advance how may used
						}
						liberty[ty][tx] = best[0];//store the best color
					}
				}
				
				int[][] newcolor = new int[cused][3];
				int adv = 0; //advaning index for newcolor
				for(int i=0;i<finc;i++){
					if(used[i]){//is it used
						newcolor[adv] = fnal[i];//fill in the new color with a found final color
						adv++;//advance lever
					}
				}
				
				int tindex = 0;//index of taken
				for(int i=0;i<finc;i++){
					boolean isr = false;
					for(int ty=0;ty<ybound;ty++){
						for(int tx=0;tx<xbound;tx++){
							if(liberty[ty][tx]==i){//match reduced colors to recuced color list
								liberty[ty][tx]=tindex;
								isr = true;
							}
						}
					}
					if(isr){
						tindex++;
					}
				}
				
				//write (NEW)colors to binary
				binary += doubin(cused-1,binfinc);//write how many colors
				for(int i=0;i<cused;i++){
					binary += twelvebitrgb(newcolor[i]);
				}
				binary += doubin(ybound-1,6);//write the bounds
				binary += doubin(xbound-1,6);
				
				int newfinc = (int)Math.ceil(Math.log(cused)/Math.log(2));//how many bits to store NEW colors

				//third runthrough to put colors in
				for(int ty=0;ty<ybound;ty++){
					for(int tx=0;tx<xbound;tx++){
						binary += doubin(liberty[ty][tx],newfinc);//write the color
					}
				}
			}
		}
		writetri(binary);
	}
	
	public Compress(){
		
		BufferedImage out = null;
		try{
			out = ImageIO.read(new File(path));
		}catch(Exception ex){}
		fourtwo(out);
		
		disp = readtri("C:\\Users\\Emmett\\Desktop\\source - texture\\compress\\treeriver0.tri");
		new Wind();
		//savedisp();
	}
	
	public void savedisp(){
		String out = "";
		String[] ba = bases(path);
		int c=0;
		while(true){
			out = ba[0]+ba[1]+"render"+c+ba[2];
			if(!new File(out).exists()){
				break;
			}
			c++;
		}
		try{
			ImageIO.write(disp, "png", new File(out));
		}catch(Exception ex){}
	}
	
	public static void main(String[] args) {
		new Compress();
	}
}