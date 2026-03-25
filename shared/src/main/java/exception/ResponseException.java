package exception;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception {

    public enum Code {
        ServerError,
        ClientError,
    }

    final private Code code;

    public ResponseException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", code));
    }

    public static ResponseException fromJson(String json, int httpStatus) {
        var map = new Gson().fromJson(json, HashMap.class);
        String message = "Unknown error";

        if (map.get("message") != null){
            message = map.get("message").toString();
        }

        Code code = fromHttpStatusCode(httpStatus);

        return new ResponseException(code, message);
    }

    public static Code fromHttpStatusCode(int httpStatusCode) {
        if (httpStatusCode >= 500) {return Code.ServerError;}
        else {return Code.ClientError;}
    }

}
