package inkscape2physics;
import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;
//import org.apache.xerces.parsers.SAXParser;

/** converts SVG to Java code generating a (physics) level.
* polygon = genBackground
* circle = genCircle
* rectangle = genRect
* text = genSpecial
*/
public class InkscapetoPhysics extends DefaultHandler {

	StringBuffer code=new StringBuffer();

	String layer=null;

	final static String ignorelayer="sky";

	float [] layerofs=null;

	int g_level=0;

	private float[] multvm(float [] vec,float [] m) {
		return new float[] {
			m[0]*vec[0] + m[2]*vec[1] + m[4],
			m[1]*vec[0] + m[3]*vec[1] + m[5]
		};
	}

	public void startElement(String namespaceURI, String localName,
	String qName, Attributes atts) {
		// some universally needed attributes
		float [] ofs = getTransform(atts.getValue("","transform"));
		int fillcol = getFillColor(atts.getValue("","style"));
		String colstr = "-1";
		if (fillcol != -1) colstr = "0x"+Integer.toString(fillcol,16);
		//colstr= "new short[]{"+fillcol[0]+","+fillcol[1]+","+fillcol[2]+"}";
		if (qName.equals("g")) {
			g_level++;
			//System.out.println("XXX"+g_level);
			if (g_level==1) {
				// get layer id (id)
				layer=atts.getValue("inkscape:label");
				layerofs = ofs;
				//System.out.println("tr"+trans[0]+" "+trans[1]);
				return;
			}
		}
		if (layer==null) return; // paths outside layers are assumed to be
		// clip paths
		if (layer.equals(ignorelayer)) return;
		if (qName.equals("path") 
		&&    atts.getValue("","sodipodi:type")!=null
		&&    atts.getValue("","sodipodi:type").equals("arc") ) {
			float x=Float.parseFloat(atts.getValue("","sodipodi:cx"));
			float y=Float.parseFloat(atts.getValue("","sodipodi:cy"));
			float width=Float.parseFloat(atts.getValue("","sodipodi:rx"));
			float height=Float.parseFloat(atts.getValue("","sodipodi:ry"));
			float [] vt = multvm(multvm(new float[]{x,y},layerofs),ofs);
			code.append("\t\tw.genCircle(\""+layer+"\","+colstr+",");
			code.append((vt[0])+"f,"+(vt[1])+
				"f,"+((width+height)/2f)+"f);\n");
		} else if (qName.equals("path")) {
			float x=0,y=0,newx,newy;
			String d=atts.getValue("","d");
			//System.out.println("ofs "+ofs[0]+" "+ofs[1]);
			//code.append("\nd="+d);
			code.append("\t\tw.genPolygon(\""+layer+"\","+colstr+",");
			code.append("new float[] {");
			// format of d:
			// 'm' ' ' ( ('l')? <x> ',' <y> )* ' ' 'z'
			String [] par = d.split("[ ,zZ]+");
			int i=0;
			boolean relative=false;
			while (i<par.length) {
				if (par[i].length()==0) continue;
				if (par[i].equals("c") || par[i].equals("C")) {
					throw new Error("curve found in path - not supported!");
				} else if (par[i].equals("m") || par[i].equals("l")) {
					relative=true;
					i++;
				} else if (par[i].equals("M") || par[i].equals("L")) {
					relative=false;
					i++;
				}
				newx = Float.parseFloat(par[i++]);
				newy = Float.parseFloat(par[i++]);
				if (relative) {
					newx += x;
					newy += y;
				}
				x = newx;
				y = newy;
				float [] vt = multvm(multvm(new float[]{x,y},layerofs),ofs);
				code.append((vt[0]) + "f," + (vt[1]) + "f");
				if (i<par.length-1) code.append(",");
			}
			code.append("});\n");
		} else if (qName.equals("text")) {
			textofs = ofs;
			textcolstr = colstr;
			//textofs = getTransform(atts.getValue("transform"));
		} else if (qName.equals("tspan")) {
			textx=atts.getValue("","x");
			texty=atts.getValue("","y");
			chars="";
		} else if (qName.equals("rect")) {
			float x=Float.parseFloat(atts.getValue("","x"));
			float y=Float.parseFloat(atts.getValue("","y"));
			float width=Float.parseFloat(atts.getValue("","width"));
			float height=Float.parseFloat(atts.getValue("","height"));
			// rect will only work with rotate/scale/translate,
			// not with shear and other transforms
			float transx = layerofs[4] + ofs[4];
			float transy = layerofs[5] + ofs[5];
			float scalex = layerofs[0] * ofs[0];
			float scaley = layerofs[3] * ofs[3];
			// rotate requires acos, asin, or atan, not available on midp,
			// so we do not support it yet
			if (layerofs[1] != 0 || layerofs[2]!=0
			||  ofs[1] != 0 || ofs[2]!=0)
				throw new Error("Rect rotation not supported!");
			code.append("\t\tw.genRect(\""+layer+"\","+colstr+",");
			code.append((scalex*width/2f + scalex*x + transx)+"f,");
			code.append((scaley*height/2f + scaley*y + transy)+"f,");
			code.append(scalex*width+"f,");
			code.append(scaley*height+"f,0f);\n");
		}
	}

	/** returns int[6] transformation matrix. Recognises translate, scale,
	 * and matrix directives.
	 * For translate, ret[0...3] == Id, and ret[4] = xtranslate,
	 * ret[5] = ytranslate. 
	 * For scale, ret[0] = scalex, ret[3]=scaley, the others are 0 */
	float [] getTransform(String t) {
		if (t==null) return new float[] {1f,0f,0f,1f,0f,0f};
		if (t.indexOf("matrix")==0) {
			String [] te = t.split("matrix\\(|,|\\)| ");
			if (te.length != 7) throw new Error("Unexpected transform "+t);
			return new float[] { 
				Float.parseFloat(te[1]),
				Float.parseFloat(te[2]),
				Float.parseFloat(te[3]),
				Float.parseFloat(te[4]),
				Float.parseFloat(te[5]),
				Float.parseFloat(te[6])
			};
		} else if (t.indexOf("scale")==0) {
			String [] te = t.split("scale\\(|,|\\)| ");
			if (te.length != 3) throw new Error("Unexpected transform "+t);
			return new float[] {
				Float.parseFloat(te[1]),
				0f,0f,
				Float.parseFloat(te[2]),
				0f,0f
			};
		} else if (t.indexOf("translate")==0) {
			String [] te = t.split("translate\\(|,|\\)| ");
			if (te.length != 3) throw new Error("Unexpected transform "+t);
			return new float[] { 1f,0f,0f,1f,
				Float.parseFloat(te[1]),Float.parseFloat(te[2])};
		} else {
			//return new float[] {0f,0f}; // unknown transform
			throw new Error("Unexpected transform "+t);
		}
	}

	/** Looks for 'fill:#.....'.   Returns -1 or 24 bit pixvalue*/
	int getFillColor(String t) {
		if (t==null) return -1;
		int idx = t.indexOf("fill:#");
		if (idx<0) return -1;
		String col = t.substring(idx+6,idx+12);
		return Integer.parseInt(col.substring(0,6),16);
		/* old version returns null or R,G,B values (0-255). */
		/*return new int[] {
			Integer.parseInt(col.substring(0,2),16),
			Integer.parseInt(col.substring(2,4),16),
			Integer.parseInt(col.substring(4,6),16)
		};*/
	}

	String chars;

	String textx,texty;

	float [] textofs;
	String textcolstr;

	public void endElement(String namespaceURI, String localName,
	String rawName) throws SAXException {
		//System.out.println("###"+rawName);
		if (rawName.equals("g")) {
			g_level--;
			//System.out.println("###"+g_level);
		}
		if (layer==null || layer.equals(ignorelayer)) return;
		if (rawName.equals("tspan")) {
			float x = Float.parseFloat(textx);
			float y = Float.parseFloat(texty);
			float [] vt = multvm(multvm(new float[]{x,y},layerofs),textofs);
			code.append("\t\tw.genSpecial(\""+layer+"\",\""+chars+"\",");
			code.append(textcolstr+","+vt[0]+"f,"+vt[1]+"f);\n");
		}
	}

	public void characters(char[] text, int start, int length) 
	throws SAXException {
		chars = new String(text).substring(start,start+length);
	}

	public static void main(String[] args) {
		InkscapetoPhysics handler = new InkscapetoPhysics();
		if (args.length != 3) {
			System.err.println("Usage: inkscape2physics [filename.svg]"
				+ " [packagename] [classname]");
			System.exit(1);
		}
		String filename = args[0];
    	//SAXParser p = new SAXParser();
    	//p.setContentHandler(handler);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			parser.parse(filename, handler);
			//p.parse(filename);
			System.out.println(
			 "package "+args[1]+";\n"
			+"public class "+args[2]+" implements GenPhysWorld {\n"
			+"\tpublic void generate(PhysicsWorld w) {\n"
			);
			System.out.println(handler.code.toString());
			System.out.println(
			 "\t}\n}\n"
			);
		} catch(Exception e) {
			String errorMessage = "Error parsing " + filename + ": " + e;
			System.err.println(errorMessage);
			e.printStackTrace();
		}
	}
}

