package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ressource.ExceptionNonContains;
import application.CameraManager;

public class Controlleur implements Initializable{
	
	private static final float TEXTURE_LAT_OFFSET = -0.2f;
    private static final float TEXTURE_LON_OFFSET = 2.8f;
	
    private ModelEarth model = new ModelEarth();
	@FXML 
	private Pane pane3D;

	@FXML
	private RadioButton buttonHisto;
	
	@FXML
	private ToggleGroup group1;
	
	@FXML
	private RadioButton buttonQuadri;
	
	@FXML
	private ChoiceBox choiceAnnee;
	
	@FXML
	private Label latitude;
	
	@FXML 
	private Label longitude;
	
	@FXML
	private Button graphe;
	
	@FXML
	private TextField vitesse;
	private EventHandler vitesseListener;
	private float vitesseActuel = 10.f;
	
	@FXML
	private Button animer;
	
	@FXML
	private Button pause;
	
	@FXML
	private Button reprend;
	
	@FXML
	private Rectangle legendMax;
	
	@FXML
	private Rectangle legendMoyPositive;
	
	@FXML
	private Rectangle legendMoyenNegatif;
	
	@FXML
	private Rectangle legendMin;
	
	@FXML
	private Label annee;
	
	private AnimationTimer animation;
	
	@FXML
	private LineChart<?, ?> graphique;

	/*@FXML
	private CategoryAxis x;

	@FXML
	private NumberAxis y;
	 */
	
	public Controlleur() {
	}
	 
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		Group root3D = new Group();
		
		//Add earth
		ObjModelImporter objImporter = new ObjModelImporter();
	    try {
	        URL modelUrl = this.getClass().getResource("Earth/earth.obj");
	        objImporter.read(modelUrl);
	        }catch(ImportException e) {
	        	System.out.println(e.getMessage());
	        }
	    MeshView[] meshViews = objImporter.getImport();
	    Group earth = new Group(meshViews);
	    root3D.getChildren().add(earth);
	    
	    //Add a camera group
	    PerspectiveCamera camera = new PerspectiveCamera(true);
	    new CameraManager(camera, pane3D, root3D);

	    // Add point light
	    PointLight light = new PointLight(Color.WHITE);
	    light.setTranslateX(-180);
	    light.setTranslateY(-90);
	    light.setTranslateZ(-120);
	    root3D.getChildren().add(light);
	        
	    // Add ambient light
	    AmbientLight ambientLight = new AmbientLight(Color.WHITE);
	    ambientLight.getScope().addAll(root3D);
	    root3D.getChildren().add(ambientLight);
	    
	    

	    // Create scene
	    SubScene subscene = new SubScene(root3D, 550, 560, true, SceneAntialiasing.BALANCED);
	    subscene.setCamera(camera);
	    subscene.setFill(Color.GREY);
	    pane3D.getChildren().addAll(root3D, subscene);
	    
	    //creer des materiels pour pouvoir creer des histogrammes ou quadrilaterals
	    final PhongMaterial redMaxMaterial = new PhongMaterial();
		Color rm = (Color) legendMax.getFill();
		redMaxMaterial.setDiffuseColor(rm);
		redMaxMaterial.setSpecularColor(rm);
		      
		final PhongMaterial redMoyMaterial = new PhongMaterial();
		Color rmoy = (Color) legendMoyPositive.getFill();
		redMoyMaterial.setDiffuseColor(rmoy);
		redMoyMaterial.setSpecularColor(rmoy); 
		        
		final PhongMaterial blueMoyMaterial = new PhongMaterial();
		Color bmoy = (Color) legendMoyenNegatif.getFill();
		blueMoyMaterial.setDiffuseColor(bmoy);
		blueMoyMaterial.setSpecularColor(bmoy);
		        
		final PhongMaterial blueMaxMaterial = new PhongMaterial();
		Color bmax = (Color) legendMin.getFill();
		blueMaxMaterial.setDiffuseColor(bmax);
		blueMaxMaterial.setSpecularColor(bmax);
		
		final PhongMaterial NanMaterial = new PhongMaterial();
		Color nan = new Color(0.5,0.5,0.5,0.3);
		NanMaterial.setDiffuseColor(nan);
		NanMaterial.setSpecularColor(nan);
		
		//creer un nouveau group pour enregistrer les histogrammes ou quadrilateral
		Group QuadriEtHisto = new Group();
		root3D.getChildren().add(QuadriEtHisto);
		
		buttonQuadri.setOnAction(new EventHandler<ActionEvent>() {

	        @Override
	        public void handle(ActionEvent event) {
	        	int year = Integer.parseInt(annee.getText());
                try {
                	QuadrilateralChange(QuadriEtHisto, year, redMaxMaterial, redMoyMaterial, blueMoyMaterial, blueMaxMaterial, NanMaterial);
				} catch (ExceptionNonContains e) {
					e.printStackTrace();
				}
                event.consume();
	        }
		});
		buttonHisto.setOnAction(new EventHandler<ActionEvent>() {

	        @Override
	        public void handle(ActionEvent event) {
	        	int year = Integer.parseInt(annee.getText());
                try {
                	HistogrammeChange(QuadriEtHisto, year, redMaxMaterial, blueMaxMaterial, NanMaterial);
				} catch (ExceptionNonContains e) {
					e.printStackTrace();
				}
                event.consume();
	        }
		});
	        
	    ArrayList<Integer> listeAnnee = model.getListeAnnee();
	    choiceAnnee.setItems(FXCollections.observableArrayList(listeAnnee));
		choiceAnnee.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue ov, Number value, Number no) {
				int date = listeAnnee.get(no.intValue());
				String dateString = Integer.toString(date);
				annee.setText(dateString);
				String option = ((RadioButton)group1.getSelectedToggle()).getText();
				//mettre a jour avec la nouvelle annee choisie en fonction des options selectionnees
				if(option.equals(buttonHisto.getText())){ 
					try {
						HistogrammeChange(QuadriEtHisto, date, redMaxMaterial, blueMaxMaterial, NanMaterial);
					} catch (ExceptionNonContains e) {
						e.printStackTrace();
					}
				}else if(option.equals(buttonQuadri.getText())) {
					try {
						QuadrilateralChange(QuadriEtHisto, date, redMaxMaterial, redMoyMaterial, blueMoyMaterial, blueMaxMaterial, NanMaterial);
					} catch (ExceptionNonContains e) {
						e.printStackTrace();
					}
				}
			}
		});	
		
		vitesseListener = new EventHandler<KeyEvent>() {
			  @Override
			  public void handle(KeyEvent e) {
				  
					  String value = vitesse.getText();
					  try {
						  vitesseActuel = new Float(value);
	                      } catch (Exception exp) {
	                      		vitesse.setText("");
	                      	}
	           }
	       };
	       
		animer.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				final long start = System.nanoTime();
				animation = new AnimationTimer() {
		        	public void handle(long currentNanoTime) {
		        		int currentYear = 1880;
		        		Point3D axe = new Point3D(0,1,0);
		        		double t = (currentNanoTime - start)/1000000000.0;
		        		if(buttonHisto.isSelected()){
		        			while(currentYear <= 2020) {
		        				annee.setText(Integer.toString(currentYear));
		        				try {
									HistogrammeChange(QuadriEtHisto, currentYear, redMaxMaterial, blueMaxMaterial, NanMaterial);
								} catch (ExceptionNonContains e) {
									e.printStackTrace();
								}

		        				//earth.setRotationAxis(axe);
		        				//earth.setRotate(vitesseActuel * t);
		        				long futur = System.nanoTime()+500;
		        				if(System.nanoTime() >= futur) {
		        					currentYear++;
		        				}
		        				
		        				
		        			}
		        			this.stop();
		        			
		        		}else if(buttonQuadri.isSelected()) {
		        			while(currentYear <= 2020) {
		        				annee.setText(Integer.toString(currentYear));
		        				try {
		        					QuadrilateralChange(QuadriEtHisto, currentYear, redMaxMaterial, redMoyMaterial, blueMoyMaterial, blueMaxMaterial, NanMaterial);
								} catch (ExceptionNonContains e) {
									e.printStackTrace();
								}
		        				//earth.setRotationAxis(axe);
		        				//earth.setRotate(vitesseActuel * t);
		        				long futur = System.nanoTime()+10;
		        				if(System.nanoTime() >= futur) {
		        					currentYear++;
		        				}
		        				
		        			}
		        			this.stop();
		        			
		        		}
		        		
		        	}

				};
				animation.start();
				event.consume();
			}
		});
		
		pause.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				animation.stop();
			}
		});
		
		reprend.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				animation.start();
			}
		});
		
		earth.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                PickResult res = event.getPickResult();
                getCoordSouris(res);
            }
        });
		
		 //essayers de faire d'afficher une nouvelle fenetre avec le bouton graphe, or apparement cela produit des problemes avec cameraManager
		graphe.setOnMouseClicked((event) -> {
		    try {
		    	String lat = latitude.getText();
		    	String lon = longitude.getText();
		    	String coord = lat + "," + lon;
		    	ArrayList<Float> listTemp = model.getAnomalieParCoord(coord);
		    	XYChart.Series series = new XYChart.Series();
		    	int i = 0;
		    	for(int year = 1880; year < 2021; year++) {
		    		float temp = (float)listTemp.get(i);
		    		series.getData().add(new XYChart.Data(12, 15));
		    		i++;
		    	}
		    	graphique.getData().add(series);
		        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("graphique2D.fxml"));
		        Parent root2D = (Parent)fxmlLoader.load();
		        Scene scene = new Scene(root2D);
		        Stage stage = new Stage();
		        stage.setTitle("Graphique de l'evolution des anomalies par annee");
		        stage.setScene(scene);
		        stage.initModality(Modality.APPLICATION_MODAL);
    			stage.initStyle(StageStyle.UNDECORATED);
		        stage.show();
		    } catch (IOException e) {
		        Logger logger = Logger.getLogger(getClass().getName());
		        logger.log(Level.SEVERE, "Failed to create new Window.", e);
		    }catch (ExceptionNonContains e2) {
		    	e2.getMessage();
		    }
		});
		
	}
	
	
	public static Point3D geoCoordTo3dCoord(float lat, float lon, float radius) {
        float lat_cor = lat + TEXTURE_LAT_OFFSET;
        float lon_cor = lon + TEXTURE_LON_OFFSET;
        return new Point3D(
                -java.lang.Math.sin(java.lang.Math.toRadians(lon_cor))
                        * java.lang.Math.cos(java.lang.Math.toRadians(lat_cor))*radius,
                -java.lang.Math.sin(java.lang.Math.toRadians(lat_cor))*radius,
                java.lang.Math.cos(java.lang.Math.toRadians(lon_cor))
                        * java.lang.Math.cos(java.lang.Math.toRadians(lat_cor))*radius);
    }
	
	private void AddQuadrilateral(Group parent, Point3D topRight, Point3D bottomRight, Point3D bottomLeft, Point3D topLeft, PhongMaterial material)
    {
        final TriangleMesh triangleMesh = new TriangleMesh();
        final float[] points = {
                (float)topRight.getX(), (float)topRight.getY(), (float)topRight.getZ(),
                (float)topLeft.getX(), (float)topLeft.getY(), (float)topLeft.getZ(),
                (float)bottomLeft.getX(), (float)bottomLeft.getY(), (float)bottomLeft.getZ(),
                (float)bottomRight.getX(), (float)bottomRight.getY(), (float)bottomRight.getZ(),
        };
        final float[] texCoords = {
                1, 1,
                1, 0,
                0, 1,
                0, 0
        };
        final int[] faces = {
                0, 1, 1, 0, 2, 2,
                0, 1, 2, 2, 3, 3
        };
        
        triangleMesh.getPoints().setAll(points);
        triangleMesh.getTexCoords().setAll(texCoords);
        triangleMesh.getFaces().setAll(faces);
        
        final MeshView meshView = new MeshView(triangleMesh);
        meshView.setMaterial(material);
        parent.getChildren().addAll(meshView);
    }
	
	public void QuadrilateralChange(Group root, int a, PhongMaterial red, PhongMaterial redMoy, PhongMaterial blueMoy, PhongMaterial blue, PhongMaterial nan) throws ExceptionNonContains {
		int init = root.getChildren().size();
		root.getChildren().remove(0, init);
		for(int latitude = -88; latitude < 90; latitude += 4) {
        	for(int longitude = -178; longitude < 180; longitude += 4) {
        		String lat = Integer.toString(latitude);
        		String lon = Integer.toString(longitude);
        		String localisation = lat+","+lon;
        		float t = model.getTemp(a, localisation);
        		
        		Point3D topRight = geoCoordTo3dCoord((float)latitude, (float)longitude+4, 1.05f);
        		Point3D topLeft = geoCoordTo3dCoord((float)latitude, (float)longitude, 1.05f);
        		Point3D bottomRight = geoCoordTo3dCoord((float)latitude -4, (float)longitude +4, 1.05f);
        		Point3D bottomLeft = geoCoordTo3dCoord((float)latitude-4, (float)longitude, 1.05f);
        		
        		if(Float.isNaN(t)) {
        			AddQuadrilateral(root, topRight, bottomRight, bottomLeft, topLeft, nan);
        		}else if(t < 0 && t > -2) {
        			AddQuadrilateral(root, topRight, bottomRight, bottomLeft, topLeft, blueMoy);
        		}else if(t <= -2) {
        			AddQuadrilateral(root, topRight, bottomRight, bottomLeft, topLeft, blue);
        		}else if(t >= 0 && t < 2) {
        			AddQuadrilateral(root, topRight, bottomRight, bottomLeft, topLeft, redMoy);
        		}else {
        			AddQuadrilateral(root, topRight, bottomRight, bottomLeft, topLeft, red);
        		}
        		
        	}
		}
		
		
	}
	
	public Cylinder createLine(Point3D origin, Point3D target) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();

        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        Cylinder line = new Cylinder(0.01f, height);

        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);

        return line;
    }

	public void HistogrammeChange(Group root, int a, PhongMaterial red, PhongMaterial blue, PhongMaterial nan) throws ExceptionNonContains {
		int init = root.getChildren().size();
		root.getChildren().remove(0, init);
		for(int latitude = -88; latitude < 90; latitude += 4) {
        	for(int longitude = -178; longitude < 180; longitude += 4) {
        		String lat = Integer.toString(latitude);
        		String lon = Integer.toString(longitude);
        		String localisation = lat+","+lon;
        		float t = model.getTemp(a, localisation);
        		float radius = t * 0.1f + 1.f;
        		Point3D origin = geoCoordTo3dCoord((float)latitude, (float)longitude, 1.0f);
        		Point3D target = geoCoordTo3dCoord((float)latitude, (float)longitude, radius);
        		Cylinder line = createLine(origin, target);
        		if(t < 0) {
        			line.setMaterial(blue);
        		}else if(t > 0) {
        			line.setMaterial(red);
        		}else {
        			line.setMaterial(nan);
        		}
        		root.getChildren().add(line);
        	}
		}
		
	}
	
	public void getCoordSouris(PickResult res) {
		Point3D coords = res.getIntersectedPoint();
		int lat = (int)  Math.round(-Math.toDegrees(Math.asin(coords.getY() / Math.sqrt(Math.pow(coords.getX(), 2) + Math.pow(coords.getY(), 2) + Math.pow(coords.getZ(), 2))) ) /4)*4;
        int lon = (int)  Math.round(-Math.toDegrees(Math.atan2(coords.getX(), coords.getZ()) ) /4)*4;
        latitude.setText(Integer.toString(lat));
        longitude.setText(Integer.toString(lon));
	}
	
}
