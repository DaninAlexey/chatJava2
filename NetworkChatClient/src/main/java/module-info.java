module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.danin.NetworkCatClient to javafx.fxml;
    exports ru.danin.NetworkCatClient;
}