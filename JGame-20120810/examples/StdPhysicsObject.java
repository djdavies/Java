package examples;

import java.util.Vector;
import java.util.Enumeration;

import jgame.*;
import jgame.platform.*;

import org.jbox2d.common.*;
import org.jbox2d.collision.*;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.joints.*;

/** A standard physics object. Set the world first before creating any objects.
* Also contains some convenience functions for manipulating the Box2D world.
*/
public class StdPhysicsObject extends JGObject {
	/** Pixels / PXMUL = world units. */
	public static float PXMUL = 20f;
	public static World world = null;

	public static void setWorld(World world) {
		StdPhysicsObject.world = world;
	}

	public static World createWorld(float lowerx,float lowery,float upperx,
	float uppery) {
		AABB m_worldAABB = new AABB();
		m_worldAABB.lowerBound = new Vec2(lowerx,lowery);
		m_worldAABB.upperBound = new Vec2(upperx,uppery);
		// gravity is 0 by default. use world.setGravity to change it.
		Vec2 gravity = new Vec2(0.0f, 0.0f);
		return new World(m_worldAABB, gravity, true /*doSleep*/);
	}

	public static Body createRect(float x,float y,float width,float height,
	float rot, float mass) {
		PolygonDef sd = new PolygonDef();
		sd.setAsBox(width/PXMUL, height/PXMUL);
		sd.density = mass;
		BodyDef bd = new BodyDef();
		bd.position = new Vec2(x/PXMUL, y/PXMUL);
		bd.angle = rot;
		Body ret = world.createBody(bd);
		ret.createShape(sd);
		if (mass!=0) ret.setMassFromShapes();
		return ret;
	}

	/** Create revolute joint at given position (imagine sticking a pin at the
	 * given position).
	 * If no other object is passed, it is pinned to the background.
	 * If another object is passed, they are pinned to each other.
	 * If you want to draw the joint, use joint.getAnchor1() to get the
	 * joint's position.
	 * @param other  object to pin to, null = pin to background
	 * @param motorSpeed radians per second
	 */
	public Joint createRevoluteJoint(StdPhysicsObject other,float x,float y,
	boolean enableMotor,float motorSpeed,float maxMotorTorque) {
		Vec2 cen = new Vec2(x/PXMUL,y/PXMUL);
		RevoluteJointDef jointd = new RevoluteJointDef();
		jointd.enableMotor = enableMotor;
		jointd.motorSpeed = motorSpeed;
		jointd.maxMotorTorque = maxMotorTorque;
		if (other==null) {
			jointd.initialize(body, world.getGroundBody(), cen);
		} else {
			jointd.initialize(body, other.body, cen);
		}
		return world.createJoint(jointd);
	}

	/** Find objects at given position.
	public static StdPhysicsObject [] findObjectsAt(JGEngine eng,
	String objprefix, int cidmask, Vec2 cen) {
		Vector obj = eng.getObjects(objprefix,cidmask,false,null);
		StdPhysicsObject [] found = new StdPhysicsObject[2];
		for (Enumeration e=obj.elements(); e.hasMoreElements(); ) {
			StdPhysicsObject o = (StdPhysicsObject) e.nextElement();
			System.out.println("FOUND ");
			Shape os = o.body.getShapeList();
			XForm xform = o.body.getXForm();
			while (os!=null) {
				if (os.testPoint(xform,cen)) {
					if (found[0]==null) {
						found[0]=o;
					} else if (found[1]==null) {
						found[1]=o;
						break;
					}
				}
				os = os.m_next;
			}
		}
		return found;
	}*/


	/*	DistanceJointDef jointd = new DistanceJointDef();
		jointd.initialize(body,((PhysicsObject)o).body,
			new Vec2((float)x/PXMUL,  (float)y/PXMUL),
			new Vec2((float)o.x/PXMUL,(float)o.y/PXMUL) );
		jointd.collideConnected = true;
		Joint joint = world.createJoint(jointd);

		//RevoluteJointDef jointd = new RevoluteJointDef();
		//jointd.initialize(prevobj.body,thisobj.body, 
		//	new Vec2(30/PXMUL,10/PXMUL+i*40/PXMUL) );
		DistanceJointDef jointd = new DistanceJointDef();
		jointd.initialize(prevobj.body,thisobj.body,
			new Vec2(30/PXMUL, -20/PXMUL+i*40/PXMUL),
			new Vec2(30/PXMUL,  20/PXMUL+i*40/PXMUL) );
		jointd.collideConnected = true;
		Joint joint = world.createJoint(jointd);
	*/

	public ShapeDef shapedef;
	public BodyDef bodydef;
	public Body body;
	public float mass; // 0 = fixed
	public float rot;
	public float friction = 0.15f;
	public float restitution = 0.8f;
	public boolean do_rotate=true;
	// if graphic!=null, draw object as sprite
	// if graphic==null and radius!=0, draw object as circle
	// otherwise, draw object as rectangle
	float width=0,height=0;
	float radius=0;
	String physgraphic=null;
	double graphicxofs,graphicyofs;
	JGColor color=null; // null means do not draw wireframe
	int nr_rot_levels=1;
	int spritesymmetry=1;
	/** Create spherical object. Sets JGame collision bounding box to enclose
	* sphere.
	*
	* @param mass  0 = fixed
	*/
	public StdPhysicsObject(String name,int colid,double x,double y,
	double rot, double radius, double mass,double friction,double restitution) {
		super(name,true,x,y,colid,null);
		this.rot = (float)rot;
		this.radius = (float)radius;
		this.mass = (float)mass;
		this.friction = (float)friction;
		this.restitution = (float)restitution;
		CircleDef shape = new CircleDef();
		shape.radius = (float)radius/PXMUL;
		initObject(shape);
		setBBox(-(int)radius, -(int)radius,
			2*(int)radius, 2*(int)radius);
	}
	/** Create rectangular object.  JGame bounding box is empty.
	*
	* @param mass  0 = fixed
	*/
	public StdPhysicsObject(String name,int colid,double x,double y,
	double rot, double width,double height, double mass,
	double friction,double restitution) {
		super(name,true,x,y,colid,null);
		this.rot = (float)rot;
		this.width = (float)width;
		this.height = (float)height;
		this.mass = (float)mass;
		this.friction = (float)friction;
		this.restitution = (float)restitution;
		PolygonDef sd = new PolygonDef();
		sd.setAsBox((float)width/PXMUL, (float)height/PXMUL);
		initObject(sd);
		int radius = (int)(1.42*Math.max(width,height));
		setBBox(-(int)radius, -(int)radius,
			2*(int)radius, 2*(int)radius);
	}

	private void initObject(ShapeDef sd) {
		shapedef = sd;
		shapedef.density = mass;
		bodydef = new BodyDef();
		shapedef.friction = friction;
		shapedef.restitution = restitution;
		bodydef.position = new Vec2((float)x/PXMUL,(float)y/PXMUL);
		bodydef.angle = rot;
		body = world.createBody(bodydef);
		body.createShape(shapedef);
		body.setUserData(this); // for following body back to JGObject
		if (mass!=0) body.setMassFromShapes();
	}


	/** Define graphic.
	* @param col color to use if wireframe is drawn, null = no wireframe
	* @param graphic  null means revert to wireframe
	* @param xofs  graphic paint offset in pixels 
	* @param yofs  graphic paint offset in pixels 
	* @param nr_rot_levels  # of prerotated images, 1 = no prerotated images
	*/
	public void setAppearance(JGColor col,String graphic,
	double xofs, double yofs,boolean do_rotate,
	int nr_rot_levels,int spritesymmetry) {
		this.physgraphic = graphic;
		this.color = col;
		this.graphicxofs = xofs;
		this.graphicyofs = yofs;
		this.do_rotate = do_rotate;
		this.nr_rot_levels = nr_rot_levels;
		this.spritesymmetry = spritesymmetry;
	}

	public void applyBackgroundFriction(float linearfriction,
	float angulardamping) {
		Vec2 v = body.getLinearVelocity();
		v.x = -linearfriction*v.x;
		v.y = -linearfriction*v.y;
		body.applyForce(v, body.getWorldCenter() );
		body.setAngularVelocity(angulardamping*body.getAngularVelocity());
	}

	public Vec2 getSpeedPixelsPerFrame() {
		if (getLastX()==0 && getLastY()==0) return new Vec2(0,0);//first frame
		return new Vec2((float)(gamespeed*(x-getLastX())),
						(float)(gamespeed*(y-getLastY())) );
	}

	/** Make JGame position and angle track physics position. Also check if
	 * object is still in actual world. If not, destroy. */
	public void move() {
		Vec2 position = body.getPosition();
		rot = -body.getAngle();
		x = position.x*PXMUL;
		y = position.y*PXMUL;
		if (body.m_world != world) remove();
	}

	public void destroy() {
		// body may not be in actual world. If not, do not call destroyBody.
		if (body.m_world == world) {
			// also destroys associated joints
			world.destroyBody(body);
		}
	}

	double [] polyx = new double[4];
	double [] polyy = new double[4];
	public void paint() {
		if (physgraphic==null && color==null) return;
		if (radius!=0 && physgraphic==null) {
			eng.setColor(color);
			eng.drawOval(x,y,2.0f*radius,2.0f*radius,true,true);
		} else if (radius==0 && physgraphic==null) {
			eng.setColor(color);
			float cos=(float)Math.cos(do_rotate?rot:0.0);
			float sin=(float)Math.sin(do_rotate?rot:0.0);
			polyx[0]=(int)(x-width*cos-height*sin);
			polyy[0]=(int)(y+width*sin-height*cos);
			polyx[1]=(int)(x+width*cos-height*sin);
			polyy[1]=(int)(y-width*sin-height*cos);
			polyx[2]=(int)(x+width*cos+height*sin);
			polyy[2]=(int)(y-width*sin+height*cos);
			polyx[3]=(int)(x-width*cos+height*sin);
			polyy[3]=(int)(y+width*sin+height*cos);
			eng.drawPolygon(polyx,polyy,null,4,true,true);
			/*int p1x=(int)(x-width*cos-height*sin);
			int p1y=(int)(y+width*sin-height*cos);
			int p2x=(int)(x+width*cos-height*sin);
			int p2y=(int)(y-width*sin-height*cos);
			int p3x=(int)(x+width*cos+height*sin);
			int p3y=(int)(y-width*sin+height*cos);
			int p4x=(int)(x-width*cos+height*sin);
			int p4y=(int)(y+width*sin+height*cos);
			eng.drawLine(p1x,p1y,p2x,p2y);
			eng.drawLine(p2x,p2y,p3x,p3y);
			eng.drawLine(p3x,p3y,p4x,p4y);
			eng.drawLine(p4x,p4y,p1x,p1y);*/
		} else {
			if (do_rotate && nr_rot_levels>1 &&
			!eng.isAndroid() && !eng.isOpenGL()) {
				int roti = (int)Math.floor(0.5-rot*nr_rot_levels/(2.0*Math.PI));
				roti += 100000*nr_rot_levels;//ensure positive
				roti %= nr_rot_levels/spritesymmetry;
				// XXX offsets only work for square objects
				eng.drawImage(x+1.42*graphicxofs, y+1.42*graphicyofs,
					physgraphic+roti);
			} else {
			eng.drawImage(x+graphicxofs, y+graphicyofs, physgraphic,
				null, 1.0, do_rotate?rot:0.0, 1.0,true);
			}
		}
	}

}
