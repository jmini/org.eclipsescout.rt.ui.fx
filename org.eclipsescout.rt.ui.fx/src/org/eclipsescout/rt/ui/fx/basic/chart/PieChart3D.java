/*******************************************************************************
 * Copyright (c) 2014 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipsescout.rt.ui.fx.basic.chart;

 import javafx.animation.Interpolator;
 import javafx.animation.KeyFrame;
 import javafx.animation.KeyValue;
 import javafx.animation.Timeline;
 import javafx.animation.Transition;
 import javafx.beans.property.DoubleProperty;
 import javafx.beans.property.DoublePropertyBase;
 import javafx.beans.property.FloatProperty;
 import javafx.beans.property.FloatPropertyBase;
 import javafx.beans.property.ObjectProperty;
 import javafx.beans.property.ObjectPropertyBase;
 import javafx.beans.property.ReadOnlyObjectProperty;
 import javafx.beans.property.ReadOnlyObjectWrapper;
 import javafx.beans.property.SimpleObjectProperty;
 import javafx.beans.property.StringProperty;
 import javafx.beans.property.StringPropertyBase;
 import javafx.collections.ObservableList;
 import javafx.event.ActionEvent;
 import javafx.event.EventHandler;
 import javafx.geometry.Point3D;
 import javafx.scene.AmbientLight;
 import javafx.scene.Group;
 import javafx.scene.PerspectiveCamera;
 import javafx.scene.PointLight;
 import javafx.scene.SceneAntialiasing;
 import javafx.scene.SubScene;
 import javafx.scene.chart.Chart;
 import javafx.scene.control.Tooltip;
 import javafx.scene.input.MouseEvent;
 import javafx.scene.paint.Color;
 import javafx.scene.paint.PhongMaterial;
 import javafx.scene.shape.MeshView;
 import javafx.scene.shape.TriangleMesh;
 import javafx.scene.transform.Rotate;
 import javafx.scene.transform.Translate;
 import javafx.util.Duration;

 public class PieChart3D extends Chart {

   private static final float ANIMATION_DISTANCE_FACTOR = 0.2f;
   private static final double ANIMATION_DURATION = 200;
   private static final int DELTA_HUE = 40;

   private double mouseOldX;
   private double mouseOldY;
   private double mousePosX;
   private double mousePosY;
   private double mouseDeltaX;
   private double mouseDeltaY;

   private float maxRadius;

   private int currentHue;

   public final Rotate rx = new Rotate();
   public final Rotate ry = new Rotate();

   private final Group root = new Group();
   private final SubScene subScene = new SubScene(root, 0, 0, true,
       SceneAntialiasing.BALANCED);

   private final FloatProperty currentRadius = new FloatPropertyBase() {
     @Override
     public Object getBean() {
       return PieChart3D.this;
     }

     @Override
     public String getName() {
       return "currentRadius";
     }
   };

   /** PieCharts data */
   private final ObjectProperty<ObservableList<Data>> data = new ObjectPropertyBase<ObservableList<Data>>() {
     private ObservableList<Data> oldDataList;

     @Override
     protected void invalidated() {

       buildChart();

       Timeline shrinkAnimation = new Timeline(
           new KeyFrame(Duration.millis(0), new KeyValue(currentRadius, currentRadius.getValue(), Interpolator.LINEAR)),
           new KeyFrame(Duration.millis(ANIMATION_DURATION), new KeyValue(currentRadius, 0, Interpolator.LINEAR))
           );
       shrinkAnimation.setOnFinished(new EventHandler<ActionEvent>() {
         @Override
         public void handle(ActionEvent event) {
           shrinkAnimationFinished();
           oldDataList = data.getValue();
         }
       });
       shrinkAnimation.playFromStart();

     }

     private void shrinkAnimationFinished() {
//      System.out.println("oldDataList: "+oldDataList);
//      if (oldDataList != null) {
//        System.out.println("oldDataList.size: "+oldDataList.size());
//        for (Data oldData : oldDataList) {
//          // TODO
//          oldData.getCircularSector().radiusProperty().unbind();
//        }
//      }

       // remove children and add new
       root.getChildren().clear();

       PointLight light = new PointLight(new Color(0.9, 0.9, 0.9, 1));
       light.setTranslateX(100);
       light.setTranslateY(-300);
       light.setTranslateZ(-400);
       AmbientLight ambientLight = new AmbientLight(new Color(0.35, 0.35, 0.35, 1));
       root.getChildren().addAll(ambientLight, light);

       for (Data pieDdata : get()) {
         root.getChildren().add(pieDdata.getCircularSector());
       }

       Timeline expandAnimation = new Timeline(
           new KeyFrame(Duration.millis(0), new KeyValue(currentRadius, 0, Interpolator.LINEAR)),
           new KeyFrame(Duration.millis(ANIMATION_DURATION), new KeyValue(currentRadius, maxRadius, Interpolator.LINEAR))
           );
       expandAnimation.playFromStart();
     }

     @Override
     public Object getBean() {
       return PieChart3D.this;
     }

     @Override
     public String getName() {
       return "data";
     }
   };

   public final ObservableList<Data> getData() {
     return data.getValue();
   }

   public final void setData(ObservableList<Data> value) {
     data.setValue(value);
   }

   public final ReadOnlyObjectProperty<ObservableList<Data>> dataProperty() {
     return data;
   }

   public PieChart3D() {
     rx.setAxis(Rotate.X_AXIS);
     ry.setAxis(Rotate.Z_AXIS);
     rx.setAngle(-55);
     root.getTransforms().addAll(rx, ry);

     subScene.setCamera(new PerspectiveCamera());

     setOnMousePressed(new EventHandler<MouseEvent>() {
       @Override
       public void handle(MouseEvent me) {
         mousePosX = me.getSceneX();
         mousePosY = me.getSceneY();
         mouseOldX = me.getSceneX();
         mouseOldY = me.getSceneY();
       }
     });
     setOnMouseDragged(new EventHandler<MouseEvent>() {
       @Override
       public void handle(MouseEvent me) {
         mouseOldX = mousePosX;
         mouseOldY = mousePosY;
         mousePosX = me.getSceneX();
         mousePosY = me.getSceneY();
         mouseDeltaX = (mousePosX - mouseOldX);
         mouseDeltaY = (mousePosY - mouseOldY);

         double modifier = 1.0;
         double modifierFactor = 0.1;

         if (me.isControlDown()) {
           modifier = 0.1;
         }
         if (me.isShiftDown()) {
           modifier = 10.0;
         }
         ry.setAngle(ry.getAngle() - mouseDeltaX * modifierFactor
             * modifier * 2.0); // +
         rx.setAngle(rx.getAngle() + mouseDeltaY * modifierFactor
             * modifier * 2.0); // -
       }
     });

     getChartChildren().add(subScene);
   }

   public PieChart3D(ObservableList<Data> dataList) {
     this();
     setData(dataList);
   }

   private void buildChart() {
     double total = 0;
     for (Data pieData : getData()) {
       total += pieData.getPieValue();
     }

     currentHue = 0;

     float startAngle = 0;
     for (Data pieData : getData()) {
       float angle = (float) (360 * pieData.getPieValue() / total);

       Color c = Color.hsb(currentHue, 0.5, 1);
       currentHue += DELTA_HUE;
       if (currentHue >= 360) {
         currentHue = 0;
       }

       CircularSector sector = new CircularSector(angle, startAngle, c);

       if (getData().size() != 1) {
         final Translate t = new Translate(0, 0, 0);
         sector.getTransforms().add(new Rotate(-angle / 2));
         sector.getTransforms().add(t);
         sector.getTransforms().add(new Rotate(angle / 2));

         final Transition translateTransition = new Transition() {
           {
             setAutoReverse(true);
             setCycleDuration(Duration.millis(1000));
           }

           @Override
           protected void interpolate(double frac) {
             t.setX(frac * maxRadius * ANIMATION_DISTANCE_FACTOR);
           }
         };

         sector.setOnMouseEntered(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
             translateTransition.setRate(1);
             translateTransition.play();
           }
         });
         sector.setOnMouseExited(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
             translateTransition.setRate(-1);
             translateTransition.play();
           }
         });
       }

       sector.radiusProperty().bind(currentRadius);

       pieData.setCircularSector(sector);
       startAngle += angle;
     }
   }

   @Override
   protected void layoutChartChildren(double top, double left, double width,
       double height) {

     subScene.setHeight(height);
     subScene.setWidth(width);

     root.setTranslateX(width / 2);
     root.setTranslateY(height / 2);

     maxRadius = (float) (0.45 * height);
     currentRadius.setValue(maxRadius);
   }

   /**
    * PieChart3D Data Item, represents one slice in the PieChart
    */
   public final static class Data {

     private final Tooltip tooltip = new Tooltip();

     // -------------- PUBLIC PROPERTIES ------------------------------------

     /**
      * The chart which this data belongs to.
      */
     private final ReadOnlyObjectWrapper<PieChart3D> chart = new ReadOnlyObjectWrapper<PieChart3D>(
         this, "chart");

     public final PieChart3D getChart() {
       return chart.getValue();
     }

     private void setChart(PieChart3D value) {
       chart.setValue(value);
     }

     public final ReadOnlyObjectProperty<PieChart3D> chartProperty() {
       return chart.getReadOnlyProperty();
     }

     /**
      * The name of the pie slice
      */
     private StringProperty name = new StringPropertyBase() {
       @Override
       protected void invalidated() {
         buildTooltipText();
         // TODO
         // if (getChart() != null)
         // getChart().dataNameChanged(Data.this);
       }

       @Override
       public Object getBean() {
         return Data.this;
       }

       @Override
       public String getName() {
         return "name";
       }
     };

     public final void setName(String value) {
       name.setValue(value);
     }

     public final String getName() {
       return name.getValue();
     }

     public final StringProperty nameProperty() {
       return name;
     }

     /**
      * The value of the pie slice
      */
     private DoubleProperty pieValue = new DoublePropertyBase() {
       @Override
       protected void invalidated() {
         buildTooltipText();
         // TODO
         // if (getChart() != null)
         // getChart().dataPieValueChanged(Data.this);
       }

       @Override
       public Object getBean() {
         return Data.this;
       }

       @Override
       public String getName() {
         return "pieValue";
       }
     };

     public final double getPieValue() {
       return pieValue.getValue();
     }

     public final void setPieValue(double value) {
       pieValue.setValue(value);
     }

     public final DoubleProperty pieValueProperty() {
       return pieValue;
     }

     /**
      * Readonly access to the node that represents the pie slice. You can
      * use this to add mouse event listeners etc.
      */
     private ObjectProperty<CircularSector> circularSector = new SimpleObjectProperty<CircularSector>(
         this, "circularSector") {
       private CircularSector old;

       @Override
       protected void invalidated() {
         super.invalidated();

         CircularSector current = getValue();

         if (old != null) {
           Tooltip.uninstall(old, tooltip);
         }
         if (current != null) {
           Tooltip.install(current, tooltip);
         }
         old = current;
       }
     };

     public CircularSector getCircularSector() {
       return circularSector.getValue();
     }

     private void setCircularSector(CircularSector value) {
       circularSector.setValue(value);
     }

     private ObjectProperty<CircularSector> circularSectorProperty() {
       return circularSector;
     }

     // -------------- CONSTRUCTOR -------------------------------------------------

     /**
      * Constructs a PieChart3D.Data object with the given name and value.
      *
      * @param name
      *          name for Pie
      * @param value
      *          pie value
      */
     public Data(String name, double value) {
       setName(name);
       setPieValue(value);
     }

     // -------------- PUBLIC METHODS ----------------------------------------------

     /**
      * Returns a string representation of this {@code Data} object.
      *
      * @return a string representation of this {@code Data} object.
      */
     @Override
     public String toString() {
       return "Data[" + getName() + "," + getPieValue() + "]";
     }

     // -------------- PRIVATE METHODS ----------------------------------------------

     private void buildTooltipText() {
       tooltip.setText(getName() + ": " + getPieValue());
     }
   }

   public class CircularSector extends Group {

     private final static float DEFAULT_RADIUS = 200;
     private final static float DEFAULT_HEIGHT = 100;
     private final static int DELTA_ANGLE = 5;

     /**
      * The radius of the circular sector
      */
     private FloatProperty radius = new FloatPropertyBase() {
       @Override
       protected void invalidated() {
         rebuildMeshs();
       }

       @Override
       public Object getBean() {
         return CircularSector.this;
       }

       @Override
       public String getName() {
         return "radius";
       }
     };

     public final void setRadius(float value) {
       radius.setValue(value);
     }

     public final float getRadius() {
       return radius.getValue();
     }

     public final FloatProperty radiusProperty() {
       return radius;
     }

     /**
      * The angle of the circular sector
      */
     private FloatProperty angle = new FloatPropertyBase() {
       @Override
       protected void invalidated() {
         rebuildMeshs();
       }

       @Override
       public Object getBean() {
         return CircularSector.this;
       }

       @Override
       public String getName() {
         return "angle";
       }
     };

     public final void setAngle(float value) {
       angle.setValue(value);
     }

     public final float getAngle() {
       return angle.getValue();
     }

     public final FloatProperty angleProperty() {
       return angle;
     }

     /**
      * The start angle of the circular sector
      */
     private FloatProperty startAngle = new FloatPropertyBase() {
       @Override
       protected void invalidated() {
         startAngleRotation.setAngle(-get());
       }

       @Override
       public Object getBean() {
         return CircularSector.this;
       }

       @Override
       public String getName() {
         return "startAngle";
       }
     };

     public final void setStartAngle(float value) {
       startAngle.setValue(value);
     }

     public final float getStartAngle() {
       return startAngle.getValue();
     }

     public final FloatProperty startAngleProperty() {
       return startAngle;
     }

     /**
      * The height of the circular sector
      */
     private FloatProperty height = new FloatPropertyBase() {
       @Override
       protected void invalidated() {
         centerTranslation.setZ(-getValue() / 2);
         rebuildMeshs();
       }

       @Override
       public Object getBean() {
         return CircularSector.this;
       }

       @Override
       public String getName() {
         return "height";
       }
     };

     public final void setHeight(float value) {
       height.setValue(value);
     }

     public final float getHeight() {
       return height.getValue();
     }

     public final FloatProperty heightProperty() {
       return height;
     }

     /**
      * The height of the circular sector
      */
     private ObjectProperty<Color> color = new SimpleObjectProperty<Color>(this, "color");

     public final void setColor(Color value) {
       color.setValue(value);
     }

     public final Color getColor() {
       return color.getValue();
     }

     public final ObjectProperty<Color> colorProperty() {
       return color;
     }

     private TriangleMesh meshSide = new TriangleMesh();
     private TriangleMesh meshInnerSide1 = new TriangleMesh();
     private TriangleMesh meshInnerSide2 = new TriangleMesh();
     private TriangleMesh meshTop = new TriangleMesh();
     private TriangleMesh meshBottom = new TriangleMesh();

     private Rotate startAngleRotation = new Rotate(0, new Point3D(0, 0, 1));
     private Translate centerTranslation = new Translate(0, 0, 0);

     public CircularSector(float angle, float startAngle, Color color) {
       this(DEFAULT_RADIUS, DEFAULT_HEIGHT, angle, startAngle, color);
     }

     public CircularSector(float radius, float height, float angle,
         float startAngle, Color color) {
       getTransforms().addAll(centerTranslation, startAngleRotation);

       setRadius(radius);
       setHeight(height);
       setAngle(angle);
       setStartAngle(startAngle);
       setColor(color);

       PhongMaterial material = new PhongMaterial();
       material.specularColorProperty().bind(colorProperty());
       material.diffuseColorProperty().bind(colorProperty());

       // side
       MeshView meshViewSide = new MeshView(meshSide);
       meshViewSide.setMaterial(material);

       // inner side 1
       MeshView meshViewInnerSide1 = new MeshView(meshInnerSide1);
       meshViewInnerSide1.setMaterial(material);

       // inner side 2
       MeshView meshViewInnerSide2 = new MeshView(meshInnerSide2);
       meshViewInnerSide2.setMaterial(material);

       // top
       MeshView meshViewTop = new MeshView(meshTop);
       meshViewTop.setMaterial(material);

       // bottom
       MeshView meshViewBottom = new MeshView(meshBottom);
       meshViewBottom.setMaterial(material);

       this.getChildren().addAll(meshViewTop, meshViewSide,
           meshViewInnerSide1, meshViewInnerSide2, meshViewBottom);

     }

     private void rebuildMeshs() {
       float currentAngle = getAngle();
       int nr = (int) (currentAngle / DELTA_ANGLE + 1);
       if (currentAngle % DELTA_ANGLE != 0) {
         nr++;
       }
       float[] points = new float[(nr + 1) * 6];

       int ang = 0;
       int index = 0;
       while (ang < currentAngle) {
         float x = (float) Math.cos(Math.toRadians(ang)) * getRadius();
         float y = -(float) Math.sin(Math.toRadians(ang)) * getRadius();

         // top
         points[index++] = x;
         points[index++] = y;
         points[index++] = 0;

         // bottom
         points[index++] = x;
         points[index++] = y;
         points[index++] = getHeight();

         ang += DELTA_ANGLE;
       }

       float x = (float) Math.cos(Math.toRadians(currentAngle)) * getRadius();
       float y = -(float) Math.sin(Math.toRadians(currentAngle)) * getRadius();

       // last top
       points[index++] = x;
       points[index++] = y;
       points[index++] = 0;

       // lat bottom
       points[index++] = x;
       points[index++] = y;
       points[index++] = getHeight();

       // center top
       points[index++] = 0;
       points[index++] = 0;
       points[index++] = 0;

       // center bottom
       points[index++] = 0;
       points[index++] = 0;
       points[index++] = getHeight();

       float[] texCoords = new float[(nr + 1) * 2 * 2];

       int[] facesSide = new int[(nr - 1) * 6 * 2];
       int[] facesTop = new int[(nr - 1) * 6];
       int[] facesBottom = new int[(nr - 1) * 6];
       index = 0;
       int indexTop = 0;
       int indexBottom = 0;
       for (int i = 0; i < (nr - 1) * 2; i += 2) {
         // triangle top
         facesTop[indexTop++] = i;
         facesTop[indexTop++] = i;
         facesTop[indexTop++] = i + 2;
         facesTop[indexTop++] = i + 2;
         facesTop[indexTop++] = points.length / 3 - 2;
         facesTop[indexTop++] = points.length / 3 - 2;

         // triangle bottom
         facesBottom[indexBottom++] = i + 3;
         facesBottom[indexBottom++] = i + 3;
         facesBottom[indexBottom++] = i + 1;
         facesBottom[indexBottom++] = i + 1;
         facesBottom[indexBottom++] = points.length / 3 - 1;
         facesBottom[indexBottom++] = points.length / 3 - 1;

         // triangle side bottom
         facesSide[index++] = i;
         facesSide[index++] = i;
         facesSide[index++] = i + 1;
         facesSide[index++] = i + 1;
         facesSide[index++] = i + 3;
         facesSide[index++] = i + 3;

         // triangle side top
         facesSide[index++] = i;
         facesSide[index++] = i;
         facesSide[index++] = i + 3;
         facesSide[index++] = i + 3;
         facesSide[index++] = i + 2;
         facesSide[index++] = i + 2;
       }

       int[] facesInnerSide1 = new int[12];
       // triangle inner side 1 top
       facesInnerSide1[0] = 0;
       facesInnerSide1[1] = 0;
       facesInnerSide1[2] = points.length / 3 - 2;
       facesInnerSide1[3] = points.length / 3 - 2;
       facesInnerSide1[4] = points.length / 3 - 1;
       facesInnerSide1[5] = points.length / 3 - 1;

       // triangle inner side 1 bottom
       facesInnerSide1[6] = 1;
       facesInnerSide1[7] = 1;
       facesInnerSide1[8] = 0;
       facesInnerSide1[9] = 0;
       facesInnerSide1[10] = points.length / 3 - 1;
       facesInnerSide1[11] = points.length / 3 - 1;

       // triangle inner side 2 top
       int[] facesInnerSide2 = new int[12];
       facesInnerSide2[0] = points.length / 3 - 2;
       facesInnerSide2[1] = points.length / 3 - 2;
       facesInnerSide2[2] = points.length / 3 - 4;
       facesInnerSide2[3] = points.length / 3 - 4;
       facesInnerSide2[4] = points.length / 3 - 3;
       facesInnerSide2[5] = points.length / 3 - 3;

       // triangle inner side 2 bottom
       facesInnerSide2[6] = points.length / 3 - 1;
       facesInnerSide2[7] = points.length / 3 - 1;
       facesInnerSide2[8] = points.length / 3 - 2;
       facesInnerSide2[9] = points.length / 3 - 2;
       facesInnerSide2[10] = points.length / 3 - 3;
       facesInnerSide2[11] = points.length / 3 - 3;

       // side
       meshSide.getPoints().setAll(points);
       meshSide.getTexCoords().setAll(texCoords);
       meshSide.getFaces().setAll(facesSide);

       // inner side 1
       meshInnerSide1.getPoints().setAll(points);
       meshInnerSide1.getTexCoords().setAll(texCoords);
       meshInnerSide1.getFaces().setAll(facesInnerSide1);

       // inner side 2
       meshInnerSide2.getPoints().setAll(points);
       meshInnerSide2.getTexCoords().setAll(texCoords);
       meshInnerSide2.getFaces().setAll(facesInnerSide2);

       // top
       meshTop.getPoints().setAll(points);
       meshTop.getTexCoords().setAll(texCoords);
       meshTop.getFaces().setAll(facesTop);

       // bottom
       meshBottom.getPoints().setAll(points);
       meshBottom.getTexCoords().setAll(texCoords);
       meshBottom.getFaces().setAll(facesBottom);
     }
   }
 }
