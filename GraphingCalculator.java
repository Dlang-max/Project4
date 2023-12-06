import javafx.application.Application;
import javafx.scene.chart.*;
import java.util.*;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GraphingCalculator extends Application {
	public static void main (String[] args) {
		launch(args);
	}

	protected static final int WINDOW_WIDTH = 600, WINDOW_HEIGHT = 500;
	protected static final double MIN_X = -10, MAX_X = +10, DELTA_X = 0.01;
	protected static final double MIN_Y = -10, MAX_Y = +10;
	protected static final double GRID_INTERVAL = 5;
	protected static final String EXAMPLE_EXPRESSION = "2*x+5*x*x";
	protected final ExpressionParser expressionParser = new SimpleExpressionParser();

	private void graph (LineChart<Number, Number> chart, Expression expression, boolean clear) {
		final XYChart.Series series = new XYChart.Series();
		for (double x = MIN_X; x <= MAX_X; x += DELTA_X) {
			final double y = expression.evaluate(x);
			series.getData().add(new XYChart.Data(x, y));
		}
		if (clear) {
			chart.getData().clear();
		}
		chart.getData().addAll(series);
	}

	@Override
	public void start (Stage primaryStage) {
		primaryStage.setTitle("Graphing Calculator");

		final Pane queryPane = new HBox();
		final Label label = new Label("y=");
		final TextField textField = new TextField(EXAMPLE_EXPRESSION);
		final Button graphButton = new Button("Graph");
		final CheckBox diffBox = new CheckBox("Show Derivative");
		queryPane.getChildren().add(label);
		queryPane.getChildren().add(textField);

		final Pane graphPane = new Pane();
		final LineChart<Number, Number> chart = new LineChart<Number, Number>(new NumberAxis(MIN_X, MAX_X, GRID_INTERVAL), new NumberAxis(MIN_Y, MAX_Y, GRID_INTERVAL));
		chart.setLegendVisible(false);
		chart.setCreateSymbols(false);
		graphPane.getChildren().add(chart);
		graphButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			//When the button is clicked:
			public void handle (MouseEvent e) {
				try {
					final Expression expression = expressionParser.parse(textField.getText());
					graph(chart, expression, true);
					System.out.println(expression.convertToString(0));
					if (diffBox.isSelected()) {
						final Expression derivative = expression.differentiate();
						graph(chart, derivative, false);
					}
				} catch (ExpressionParseException epe) {
					textField.setStyle("-fx-text-fill: red");
				} catch (UnsupportedOperationException epe) {
					textField.setStyle("-fx-text-fill: red");
				}
			}
		});
		queryPane.getChildren().add(graphButton);
		queryPane.getChildren().add(diffBox);

		textField.setOnKeyPressed(e -> textField.setStyle("-fx-text-fill: black"));
		
		final BorderPane root = new BorderPane();
		root.setTop(queryPane);
		root.setCenter(graphPane);

		final Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
