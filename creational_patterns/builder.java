package creational_patterns;
import java.util.*;;

public class builder {

    static class HttpRequest{
        private String url;
        private String method;
        private Map<String, String> headers;
        private String body;

        private HttpRequest(){}

        public static class Builder{
            private HttpRequest req = new HttpRequest();
            
            public Builder url(String url){
                req.url = url;
                return this;
            }

            public Builder method(String method){
                req.method = method;
                return this;
            }

            public Builder header(String key, String val){
                if (req.headers == null) {
                    req.headers = new HashMap<>();
                }
                req.headers.put(key, val);
                return this;
            }

            public Builder body(String body){
                req.body = body;
                return this;
            }

            public HttpRequest build(){
                if (req.url == null) {
                    throw new IllegalStateException("Url is Required");
                }
                return req;
            }
        }
    }

    // usage
    public static void main(String[] args) {
            HttpRequest httpRequest = new HttpRequest.Builder()
                .url("http://planup.fyi")
                .method("POST")
                .header("Content-Type", "application/json")
                .body("{\"key\": \"value\"}")
                .build();
    }
}
