package seedu.address.ui;

import java.util.logging.Logger;

import javafx.animation.ScaleTransition;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;

/**
 * Panel containing the list of persons.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private static final Integer PADDING_SIZE = 80;
    private final Logger logger = LogsCenter.getLogger(PersonListPanel.class);

    @FXML
    private ListView<Person> personListView;

    @FXML
    private VBox displayView;

    private HomeCard homeCard;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public PersonListPanel(ObservableList<Person> personList, ObservableList<Person> sortedList) {
        super(FXML);
        personListView.setItems(personList);
        personListView.setCellFactory(listView -> new PersonListViewCell());
        homeCard = new HomeCard(sortedList);

        displayView.getChildren().setAll(homeCard.getRoot());

        personListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            setDisplayCard(newValue);
        });
    }

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList} and displays the {@code DisplayCard}
     * for the person in the given index.
     */
    public PersonListPanel(ObservableList<Person> personList, ObservableList<Person> sortedList, int index) {
        this(personList, sortedList);
        personListView.getSelectionModel().select(index);
    }

    private void setDisplayCard(Person person) {
        DisplayCard displayCard = new DisplayCard(person);
        displayView.getChildren().setAll(displayCard.getRoot());
        VBox.setVgrow(displayCard.getRoot(), Priority.ALWAYS);
    }

    public ListView<Person> getPersonListView() {
        return personListView;
    }

    /**
     * Resets the display view to display the home card. This is fired when the user
     * presses the escape key.
     */
    public void resetHomeCard() {
        displayView.getChildren().setAll(homeCard.getRoot());
        personListView.getSelectionModel().clearSelection();
        homeCard.playAnimation();
    }


    public int getSelectedIndex() {
        return personListView.getSelectionModel().getSelectedIndex();
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using a {@code PersonCard}.
     */
    class PersonListViewCell extends ListCell<Person> {

        private static final double HOVERED_SCALE = 1.035; // Scale for hovered item
        private static final double NORMAL_SCALE = 1.0; // Normal scale
        private static final double SCALE_DURATION = 200;
        private ScaleTransition scaleUpTransition;
        private ScaleTransition scaleDownTransition;
        private boolean hasAnimation;

        public PersonListViewCell() {
            this.hasAnimation = true;
            scaleUpTransition = new ScaleTransition(Duration.millis(SCALE_DURATION), this);
            scaleUpTransition.setToX(HOVERED_SCALE);
            scaleUpTransition.setToY(HOVERED_SCALE);
            scaleDownTransition = new ScaleTransition(Duration.millis(SCALE_DURATION), this);
            scaleDownTransition.setToX(NORMAL_SCALE);
            scaleDownTransition.setToY(NORMAL_SCALE);

            setOnMouseEntered(e -> scaleUpTransition.playFromStart());
            setOnMouseExited(e -> scaleDownTransition.playFromStart());
        }

        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
                return;
            }
            PersonCard personCard = new PersonCard(person, getIndex() + 1, getAnimateFlag());
            setGraphic(personCard.getRoot());
            personCard.getCardPane().prefWidthProperty().bind(Bindings.createDoubleBinding((
            ) -> personListView.getPrefWidth() - PADDING_SIZE, personListView.prefWidthProperty()));
        }

        private boolean getAnimateFlag() {
            if (hasAnimation) {

                hasAnimation = false;
                return true;
            }
            return hasAnimation;
        }

    }
}
