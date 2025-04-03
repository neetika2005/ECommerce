package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import java.awt.*;

public class UserInterface {

    GridPane loginPage;
    HBox headerBar;
    HBox footerBar;
    Button signInButton;
    Label welcomeLabel;
    VBox body;
    Customer loggedInCustomer;
    ProductList productList = new ProductList();
    VBox productPage;
    Button placeOrderButton = new Button("Place Order");
    ObservableList<Product> itemsInCart = FXCollections.observableArrayList();

    public BorderPane createContent(){
        BorderPane root=new BorderPane();
        root.setPrefSize(800,600);
        //root.getChildren().add(loginPage); //method to add nodes as children to Pane
        root.setTop(headerBar);
        //root.setCenter(loginPage);
        body = new VBox();
        body.setPadding(new Insets(10));
        body.setAlignment(Pos.CENTER);
        root.setCenter(body);
        productPage = productList.getAllProducts();
        body.getChildren().add(productPage);

        root.setBottom(footerBar);

        return root;
    }

    public UserInterface()
    {
        createLoginPage();
        createHeaderBar();
        createFooterBar();
    }

    private void createLoginPage(){
        Text userNameText=new Text("User Name");
        Text passwordText=new Text("Password");

        TextField userName=new TextField();
        userName.setPromptText("Type your user name here");
        PasswordField password=new PasswordField();
        password.setPromptText("Type your password here");

        Label messageLabel=new Label("Hi!");

        Button loginButton=new Button("Login");

        loginPage=new GridPane();
        //loginPage.setStyle(" -fx-background-color:grey;");
        loginPage.setAlignment(Pos.CENTER);
        loginPage.setHgap(10);
        loginPage.setVgap(10);
        loginPage.add(userNameText,0,0);
        loginPage.add(userName,1,0);
        loginPage.add(passwordText,0,1);
        loginPage.add(password,1,1);
        loginPage.add(messageLabel,0,2);
        loginPage.add(loginButton,1,2);

        //loginButton.setOnAction(e -> handleLogin(userName, password, messageLabel));
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String name=userName.getText();
                String pass = password.getText();
                Login login = new Login();
                loggedInCustomer =login.customerLogin(name, pass);
                if (loggedInCustomer != null){
                    messageLabel.setText("Welcome : " + loggedInCustomer.getName());
                    welcomeLabel.setText("Welcome-"+loggedInCustomer.getName());
                    headerBar.getChildren().add(welcomeLabel);
                    body.getChildren().clear();
                    body.getChildren().add(productPage);
                }
                else {
                    messageLabel.setText("Login Failed ! Please provide correct credentials");
                }
                //messageLabel.setText(name);
            }
        });
    }
    private void handleLogin(TextField usernameField, PasswordField passwordField, Label messageLabel) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Simple login validation (replace with actual validation logic)
        /*if (username.equals("admin") && password.equals("password")) {
            messageLabel.setTextFill(Color.GREEN); // Use javafx.scene.paint.Color instead of java.awt.Color
            messageLabel.setText("Login Successful!");
        } else {
            messageLabel.setTextFill(Color.RED); // Use javafx.scene.paint.Color instead of java.awt.Color
            messageLabel.setText("Invalid username or password.");
        }*/
    }
    private void createHeaderBar()
    {
        TextField searchBar=new TextField();
        searchBar.setPromptText("Search Here!");
        searchBar.setPrefWidth(200);

        Button searchButton=new Button("Search");

        signInButton = new Button("Sign In");
        welcomeLabel = new Label();

        Button cartButton = new Button("Cart");

        headerBar=new HBox();
        headerBar.setPadding(new Insets(10));
        headerBar.setSpacing(10);
        headerBar.setAlignment(Pos.CENTER);
        headerBar.getChildren().addAll(searchBar,searchButton, signInButton, cartButton);

        signInButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                body.getChildren().clear();//remove everything
                body.getChildren().add(loginPage);//put login page
                headerBar.getChildren().remove(signInButton);
            }
        });

        cartButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                body.getChildren().clear();
                VBox prodPage = productList.getProductsInCart(itemsInCart);
                prodPage.setAlignment(Pos.CENTER);
                prodPage.setSpacing(10);
                prodPage.getChildren().add(placeOrderButton);
                body.getChildren().add(prodPage);
                footerBar.setVisible(false);// All cases need to be handled for this
            }
        });

        placeOrderButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //need list of products and a customer
                if(itemsInCart == null){
                    //please select a product first to place order
                    showDialog("Please add some products in the cart to place order");
                    return;
                }
                if(loggedInCustomer==null) {
                    showDialog("Please login first to place order");
                    return;
                }
                int count = Order.placeMultipleOrder(loggedInCustomer,itemsInCart);
                if(count != 0){
                    showDialog("Order for "+count+" products placed successfully");
                }
                else {
                    showDialog("Order failed!!");
                }
            }
        });

    }

    private void createFooterBar()
    {
        Button buyNowButton = new Button("BuyNow");
        Button addToCartButton = new Button("Add to Cart");

        footerBar = new HBox();
        footerBar.setPadding(new Insets(10));
        footerBar.setSpacing(10);
        footerBar.setAlignment(Pos.CENTER);
        footerBar.getChildren().addAll(buyNowButton,addToCartButton);

        buyNowButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Product product = productList.getSelectedProduct();
                if(product == null){
                    //please select a product first to place order
                    showDialog("Please select a product first to place order");
                    return;
                }
                if(loggedInCustomer==null) {
                    showDialog("Please login first to place order");
                    return;
                }
                boolean status= Order.placeOrder(loggedInCustomer,product);
                if(status==true){
                    showDialog("Order placed successfully");
                }
                else {
                    showDialog("Order failed!!");
                }
            }
        });

        addToCartButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Product product = productList.getSelectedProduct();
                if(product == null){
                    //please select a product first to place order
                    showDialog("Please select a product first to add it to Cart");
                    return;
                }
                itemsInCart.add(product);
                showDialog("Selected Item has been added to the cart successfully");
            }
        });
    }

    private void showDialog(String message){
        Alert alert = new Alert((Alert.AlertType.INFORMATION));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.setTitle("Message");
        alert.showAndWait();
    }

}
