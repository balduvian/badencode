package badencode;

import java.awt.Color;
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
	String path = "C:\\Users\\ecoughlin7190\\Desktop\\colorful.jpg";
	//String path = "C:\\Users\\Emmett\\Desktop\\source - texture\\leaf-veins.jpg";
	String opath;
	int[][][] encode;
	int[][][] git;
	final int uni = 8;
	
	public String tolen(String s,int des){
		String ss = s;
		for(int i=0;i<des-ss.length();i++){
			s = "0"+s;
		}
		return s;
	}
	
	//public int[] expand(int i){
	//	
	//}
	
	public void setencode(BufferedImage b){
		encode = new int[b.getHeight()/uni][b.getWidth()/uni][3];
	}
	
	public int compcolor(Color c){//from color to byte
		String r = tolen(Integer.toBinaryString((int)Math.round((c.getRed()/255.0)*7)),3);
		String g = tolen(Integer.toBinaryString((int)Math.round((c.getGreen()/255.0)*7)),3);
		String b = tolen(Integer.toBinaryString((int)Math.round((c.getBlue()/255.0)*3)),2);
		return todec(r+g+b);
	}
	
	public int todec(String bin){//from binary to decimal
		int t=0;
		for(int i=0;i<bin.length();i++){
			t += Character.getNumericValue(bin.charAt(i))*Math.pow(2, (bin.length()-i-1));
		}
		return t;
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
	
	public void loadImages(){
		BufferedImage t = null;
		try{
			t = ImageIO.read(this.getClass().getResourceAsStream("patterns.png"));
		}catch(Exception ex){
			ex.printStackTrace();
		}
		patterns = new BufferedImage[(t.getHeight()/uni)*(t.getWidth()/uni)];
		for(int y=0;y<t.getHeight()/uni;y++){
			for(int x=0;x<t.getWidth()/uni;x++){
				patterns[y*(t.getHeight()/uni)+x] = new BufferedImage(uni,uni,BufferedImage.TYPE_BYTE_BINARY);
				for(int yy=0;yy<uni;yy++){
					for(int xx=0;xx<uni;xx++){
						patterns[y*(t.getHeight()/uni)+x].setRGB(xx, yy, t.getRGB(x*uni+xx, y*uni+yy));
					}
				}
			}
		}
	}
	
	public int getlumin(Color c){
		int[] d = {c.getRed(),c.getGreen(),c.getBlue()};
		int b = 0;
		for(int i=0;i<d.length;i++){
			if(d[i]>b){
				b = d[i];
			}
		}
		return b;
	}
	
	public int getavgl(BufferedImage b){
		int t = 0;
		for(int y=0;y<8;y++){
			for(int x=0;x<8;x++){
				t += getlumin(new Color(b.getRGB( (int)((x/8.0)*b.getWidth()) , (int)((y/8.0)*b.getHeight()) )));
			}
		}
		return t/64;
	}
	
	public BufferedImage inv(BufferedImage l){//try this only on strict bw
		for(int y=0;y<l.getHeight();y++){
			for(int x=0;x<l.getWidth();x++){
				if(l.getRGB(x, y)==-1){
					l.setRGB(x, y, 0);
				}else{
					l.setRGB(x, y, -1);
				}
			}
		}
		return l;
	}
	
	public BufferedImage onlil(BufferedImage l){
		for(int y=0;y<l.getHeight();y++){
			for(int x=0;x<l.getWidth();x++){
				int c = getlumin(new Color(l.getRGB(x, y)));
				l.setRGB(x, y, new Color(c,c,c).getRGB());
			}
		}
		return l;
	}
	
	public void writecomp(int[][][] hella){
		String[] ba = bases(path);
		File f = new File(ba[0]+ba[1]+"comp.cpc");
		try{
			FileOutputStream o = new FileOutputStream(f);
			o.write(hella.length);
			o.write(hella[0].length);
			for(int y=0;y<hella.length;y++){
				for(int x=0;x<hella[y].length;x++){
					for(int z=0;z<hella[y][x].length;z++){
						o.write(hella[y][x][z]);
					}
				}
			}
			o.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public BufferedImage ruct(File f){
		int[][][] buf = new int[0][0][0];
		try{
		FileInputStream io = new FileInputStream(f);
		buf = new int[io.read()][io.read()][3];
		int[] spc = new int[3];
		while(!(spc[2] == 0 && spc[1] == 0 && spc[0] == 0 && io.available() == 0)){
			buf[spc[0]][spc[1]][spc[2]] = io.read();
			System.out.println(spc[0] + " " + spc[1] + " " + spc[2]);
			
			spc[2]++;//advance lever
			if(spc[2]==buf[0][0].length){
				spc[2] = 0;
				spc[1]++;
				if(spc[1]==buf[0].length){
					spc[1] = 0;
					spc[0]++;
					if(spc[0]==buf.length){
						spc[0] = 0;
					}
				}
			}
			
		}
		io.close();
		}catch(Exception ex){}

		BufferedImage l = new BufferedImage(buf[0].length*uni,buf.length*uni,BufferedImage.TYPE_INT_RGB);
		for(int ty=0;ty<buf.length;ty++){
			for(int tx=0;tx<buf[0].length;tx++){
				int p = buf[ty][tx][0];
				int c0 = decon(buf[ty][tx][1]).getRGB();
				int c1 = decon(buf[ty][tx][2]).getRGB();
				for(int y=0;y<uni;y++){
					for(int x=0;x<uni;x++){
						if(patterns[p].getRGB(x, y)==-1){
							l.setRGB(tx*uni+x, ty*uni+y, c0);
						}else{
							l.setRGB(tx*uni+x, ty*uni+y, c1);
						}
					}	
				}
			}	
		}
		return l;
	}
	
	public class Wind extends JFrame{//WWWWWWWWWWIIIIIIIIIIINNNNNNNNNNNDDDDDDDDDDDDOOOOOOOOOOOOWWWWWWWWWWWWWWWW
		Can can;
		public Wind(){
			can = new Can();
			add(can);
			setSize(640,480);
			setVisible(true);
		}
		
		public class Can extends JPanel{
			public void paintComponent(Graphics g){
				super.paintComponent(g);
				try{
					g.drawImage(disp, 0, 0, null);
				}catch(Exception ex){}
				repaint();
			}
		}
	}
	
	public BufferedImage unihav(BufferedImage l){
		for(int ty=0;ty<l.getHeight()/uni;ty++){
			for(int tx=0;tx<l.getWidth()/uni;tx++){
				int t = 0;
				for(int y=0;y<uni;y++){
					for(int x=0;x<uni;x++){
						t += getlumin(new Color(l.getRGB( tx*uni+x , ty*uni+y )));
					}
				}
				t = (int)Math.round((double)t/(uni*uni));
				for(int y=0;y<uni;y++){
					for(int x=0;x<uni;x++){
						if(getlumin(new Color(l.getRGB( tx*uni+x , ty*uni+y )))>t){
							l.setRGB( tx*uni+x , ty*uni+y , -1);
						}else{
							l.setRGB( tx*uni+x , ty*uni+y , 0);
						}
					}
				}
			}
		}
		return l;
	}
	
	public BufferedImage uniscl(BufferedImage l){
		BufferedImage res = new BufferedImage((int)(Math.floor(l.getWidth()/uni)*uni),(int)(Math.floor(l.getHeight()/uni)*uni),BufferedImage.TYPE_INT_RGB);
		Graphics2D g = res.createGraphics();
		g.drawImage(l,0,0,res.getWidth(),res.getHeight(),null);
		return res;
	}
	
	public int sim(int[] a, int[] b){//returns how similar colors are smaller number, more similar;
		return Math.abs(a[0]-b[0]) + Math.abs(a[1]-b[1]) + Math.abs(a[2]-b[2]);
	}
	
	public int[] comb(int[] a,int[] b){//returns an average of two colors;;; ; ;
		return new int[]{(a[0]+b[0])/2,(a[1]+b[1])/2,(a[2]+b[2])/2};
	}
	
	public BufferedImage fourtwo(BufferedImage b){
		int t = uni/4;
		int l = uni/2;
		int ppc = uni*4; //32 pixels per chunk    32*32 = 1024 total pixels
		int chl = (ppc)/t;
		int cwl = (ppc)/l;
		int h = (int)Math.ceil((double)b.getHeight()/t);
		int w = (int)Math.ceil((double)b.getWidth()/l);
		int ch = (int)Math.ceil((double)h/chl);
		int cw = (int)Math.ceil((double)w/cwl);
		
		for(int cy=0;cy<ch;cy++){
			for(int cx=0;cx<cw;cx++){
				int colors = 32; //amount of colors
				int finc = 8; //final colors
				
				int[][] div = new int[colors][3]; //array of temp colors (stored in RGB)
				int[][] comp = new int[colors][3];//compressed color array
				int[][] fnal = new int[finc][3];//the final destination
				for(int ty=0;ty<ppc;ty++){
					for(int tx=0;tx<ppc;tx++){
						for(int i=0;i<colors;i++){
							Color cst = new Color(b.getRGB(cy*ppc+ty, cy*ppc+ty));
							int[] c = new int[]{cst.getRed(),cst.getGreen(),cst.getBlue()};
							if(div[i]==null){
								div[i] = c;
							}else if(sim(c,div[i])<50){
								div[i] = comb(c,div[i]);
							}
						}
						int lvl = 8;//level of compression
						int[][] initial = div.clone();
						int[][] allowed = new int[colors][3];//setup initial allowed
						superloop: while(true){//it's super
							int pass = 0;//how many pass
							boolean[][][] space = new boolean[lvl][lvl][lvl];//colorspace increasingly getting smaller
							int alloc = 0;//index for where allowed next should go
					
							for(int i=0;i<colors;i++){
								for(int u=0;u<3;u++){
									comp[i][u] = (int)Math.round(((double)initial[i][u]/255)*(lvl-1));//compress
								}
								if(!space[comp[i][0]][comp[i][1]][comp[i][2]]){
									allowed[alloc] = initial[i];//transfer from initial into allowed
									alloc++;//advance lever
									space[comp[i][0]][comp[i][1]][comp[i][2]] = true;//fill up space
									pass++;//one passed!
								}
							}
							
							//do some preparations
							int[][] temp = new int[pass][3];
							for(int i=0;i<pass;i++){
								temp[i] = allowed[i];
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
					}
				}
				
				for(int i=0;i<finc;i++){
					String trp = "";
					for(int u=0;u<3;u++){
						trp += fnal[i][u]+" ";
					}
					System.out.println(trp);
				}
				
				try{
					Thread.sleep(3242433);//JUST GOT BEANED
				}catch(Exception ex){}
				
				for(int ty=0;ty<chl;ty++){
					for(int tx=0;tx<cwl;tx++){
						int[] gw = new int[3];
						int ti=0;
						for(int y=0;y<t;y++){
							for(int x=0;x<l;x++){
								try{
									Color c = new Color(b.getRGB(tx*l+x, ty*t+y));
									gw[0] += c.getRed();
									gw[1] += c.getGreen();
									gw[2] += c.getBlue();
									ti++;
								}catch(Exception ex){}
							}
						}
						int n = new Color(gw[0]/ti,gw[1]/ti,gw[2]/ti).getRGB();
						for(int y=0;y<t;y++){
							for(int x=0;x<l;x++){
								try{
									b.setRGB(tx*l+x, ty*t+y, n);
								}catch(Exception ex){}
							}
						}
					}
				}
			}
		}
		
		return b;
	}
	
	public BufferedImage tricolore(BufferedImage b){
		int h = (int)Math.ceil((double)b.getHeight()/uni);
		int w = (int)Math.ceil((double)b.getWidth()/uni);
		for(int ty=0;ty<h;ty++){
			for(int tx=0;tx<w;tx++){
				int[] cum = new int[3];
				int[][][] cs = new int[uni][uni][2];
				int t = 0;
				for(int y=0;y<uni;y++){
					for(int x=0;x<uni;x++){
						try{
							Color c =  new Color(b.getRGB(tx*uni+x, ty*uni+y));
							int[] cv = {c.getRed(),c.getGreen(),c.getBlue()};
							int[] best = new int[2]; //index -- how much
							for(int i=0;i<3;i++){
								if(cv[i]>best[1]){
									best[0] = i;
									best[1] = cv[i];
								}
							}
							cs[y][x] = new int[]{best[0],cv[best[0]]};
							cum[0] += cv[0];
							cum[1] += cv[1];
							cum[2] += cv[2];
							t++;
						}catch(Exception ex){}
					}
				}
				int[] pos = {(cum[0]/(t)),(cum[1]/(t)),(cum[2]/(t))};
				for(int y=0;y<uni;y++){
					for(int x=0;x<uni;x++){
						try{
							if(cs[y][x][0]==0){
								if(cs[y][x][1]>pos[0]){
									b.setRGB(tx*uni+x,ty*uni+y,0);
								}else{
									b.setRGB(tx*uni+x,ty*uni+y,10000);
								}
							}else if(cs[y][x][0]==1){
								if(cs[y][x][1]>pos[1]){
									b.setRGB(tx*uni+x,ty*uni+y,10000);
								}else{
									b.setRGB(tx*uni+x,ty*uni+y,546365);
								}
							}else if(cs[y][x][0]==2){
								if(cs[y][x][1]>pos[2]){
									b.setRGB(tx*uni+x,ty*uni+y,546365);
								}else{
									b.setRGB(tx*uni+x,ty*uni+y,0);
								}
							}
						}catch(Exception ex){}
					}
				}
			}
		}
		return b;
	}
	
	public void makeImages(){
		int[] universe = new int[256];
		for(int i=0;i<universe.length;i++){
			
		}
		
	}
	
	public BufferedImage patternize(BufferedImage l){
		Graphics2D g = l.createGraphics();
		for(int ty=0;ty<l.getHeight()/uni;ty++){//tile x and y
			for(int tx=0;tx<l.getWidth()/uni;tx++){
				int[] best = {0,0};//best pattern for tile {how much by, what pattern}
				for(int i=0;i<2;i++){//inverse
					for(int p=0;p<patterns.length;p++){//patern
						int sofar = 0;
						for(int y=0;y<uni;y++){//sub x and y
							for(int x=0;x<uni;x++){
								if(i==0){
									if(l.getRGB(tx*uni+x,ty*uni+y)==patterns[p].getRGB(x,y)){
										sofar++;
									}
								}else{
									if(l.getRGB(tx*uni+x,ty*uni+y)!=patterns[p].getRGB(x,y)){
										sofar++;
									}
								}
							}
						}
						if(sofar>best[0]){
							best = new int[]{sofar,p};
						}
					}
				}
				g.drawImage(patterns[best[1]], tx*uni, ty*uni, null);
				encode[ty][tx][0] = best[1];//ENCODING
			}
		}
		return l;
	}
	
	public BufferedImage havize(BufferedImage l){
		int thresh = getavgl(l);
		for(int y=0;y<l.getHeight();y++){
			for(int x=0;x<l.getWidth();x++){
				if(getlumin(new Color(l.getRGB(x, y)))>thresh){
					l.setRGB(x, y, -1);
				}else{
					l.setRGB(x, y, 0);
				}
			}
		}
		return l;
	}
	
	public BufferedImage dcopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	public BufferedImage colorize(BufferedImage l,BufferedImage c){
		for(int ty=0;ty<l.getHeight()/uni;ty++){
			for(int tx=0;tx<l.getWidth()/uni;tx++){
				int[][] sq = new int[2][3];
				int[] times = new int[2];
				for(int y=0;y<uni;y++){
					for(int x=0;x<uni;x++){
						Color cn = new Color(c.getRGB(tx*uni+x, ty*uni+y));
						if(l.getRGB(tx*uni+x, ty*uni+y)==-1){
							times[0]++;
							sq[0][0] += cn.getRed();
							sq[0][1] += cn.getGreen();
							sq[0][2] += cn.getBlue();
						}else{
							times[1]++;
							sq[1][0] += cn.getRed();
							sq[1][1] += cn.getGreen();
							sq[1][2] += cn.getBlue();
						}
					}
				}
				for(int i=0;i<sq.length;i++){
					for(int v=0;v<sq[i].length;v++){
						if(times[i]>0){
							sq[i][v] = sq[i][v]/((times[i]));
						}
					}
				}
				Color[] sf = {new Color(sq[0][0],sq[0][1],sq[0][2]),new Color(sq[1][0],sq[1][1],sq[1][2])};
				encode[ty][tx][1] = compcolor(sf[0]);//ENCODE
				encode[ty][tx][2] = compcolor(sf[1]);//ENCODE
				for(int y=0;y<uni;y++){
					for(int x=0;x<uni;x++){
						if(l.getRGB(tx*uni+x, ty*uni+y)==-1){
							l.setRGB(tx*uni+x, ty*uni+y, sf[0].getRGB());
						}else{
							l.setRGB(tx*uni+x, ty*uni+y, sf[1].getRGB());
						}
					}
				}
			}
		}
		return l;
	}
	
	public void ytest(){
		BufferedImage out = null;

		try{
			out = ImageIO.read(new File(path));
		}catch(Exception ex){}
		
		/*out = uniscl(out);
		setencode(out);
		BufferedImage cmap = dcopy(out);
		out = unihav(out);
		out = patternize(out);
		out = colorize(out,cmap);*/
		
		//out = tricolore(out);
		out = fourtwo(out);
		
		try{
			ImageIO.write(out,"PNG",new File(opath));
		}catch(Exception ex){};
		//writecomp(encode);
	}
	
	public void ltest(){
		int dim = (int)Math.sqrt(patterns.length)*uni;
		BufferedImage out = new BufferedImage(dim,dim,BufferedImage.TYPE_BYTE_BINARY);
		Graphics2D g = out.createGraphics();
		for(int y=0;y<uni;y++){
			for(int x=0;x<uni;x++){
				g.drawImage(patterns[y*uni+x], x*uni, y*uni, null);
			}
		}
		try{
			ImageIO.write(out,"PNG",new File(opath));
		}catch(Exception ex){};
	}
	
	public Compress(){
		//loadImages();

		String[] ba = bases(path);
		int c=0;
		while(true){
			opath = ba[0]+ba[1]+"-comp"+c+ba[2];
			if(!new File(opath).exists()){
				break;
			}
			c++;
		}
		
		//testdupe();
		
		ytest();
		
		//disp = ruct(new File("C:\\Users\\ecoughlin7190\\Desktop\\colorfulcomp.cpc"));
		//new Wind();
	}
	
	public void testdupe(){
		int bs = (int)(Math.sqrt(patterns.length)*uni);
		int acr = (int)(Math.sqrt(patterns.length));
		BufferedImage outp = new BufferedImage(bs,bs,BufferedImage.TYPE_INT_RGB);
		for(int t=0;t<patterns.length;t++){
				ploop: for(int i=0;i<patterns.length;i++){
					int sy = (Math.round(t/acr)*uni);
					int sx = t%acr*uni;
					BufferedImage pnow = patterns[(sy/uni)*acr+(sx/uni)%acr];
					for(int y=0;y<uni;y++){
						for(int x=0;x<uni;x++){
							outp.setRGB(sx+x, sy+y, pnow.getRGB(x,y));
						}
					}
					
					if(i!=t){
						int reason = 2;
						for(int n=0;n<2;n++){
							if(n==1){
								BufferedImage tymp = inv(patterns[i]);
								saloop: for(int y=0;y<uni;y++){
									for(int x=0;x<uni;x++){
										if(tymp.getRGB(x, y) != patterns[t].getRGB(x, y)){
											reason--;
											break saloop;
										}
									}
								}
							}else{
								saloop: for(int y=0;y<uni;y++){
									for(int x=0;x<uni;x++){
										if(patterns[i].getRGB(x, y) != patterns[t].getRGB(x, y)){
											reason--;
											break saloop;
										}
									}
								}
							}
						}
						
						if(reason!=0){
							int c = Color.RED.getRGB();
							for(int y=0;y<uni;y++){
								for(int x=0;x<uni;x++){
									if(pnow.getRGB(x, y)==-16777216){
										outp.setRGB(sx+x, sy+y, c);
									}else{
										outp.setRGB(sx+x, sy+y, -1);
									}
								}
							}
							break ploop;
						}
							
					}
				}
		
		}
		try{
			ImageIO.write(outp,"PNG",new File(opath));
		}catch(Exception ex){}
	}
	
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
	
	public static void main(String[] args) {
		new Compress();
	}

}
