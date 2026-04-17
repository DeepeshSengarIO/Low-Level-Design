package structural_patterns;

public class decorator {
    
    interface DataSource{
        void writeData(String data);
        String readData();
    }

    static class FileDataSource implements DataSource{
        private String fileName;

        public FileDataSource(String fileName){
            this.fileName = fileName;
        }

        @Override
        public void writeData(String data) {
            // write the data
        }

        @Override
        public String readData() {
            return "data from file";
        }
        
    }

    static class EncryptionDecorator implements DataSource{
        private DataSource wrapped;

        public EncryptionDecorator(DataSource source){
            this.wrapped = source;
        }

        @Override
        public void writeData(String data) {
            String encrypt_data = encrypt(data);
            wrapped.writeData(encrypt_data); // Delegate to wrapped object
        }

        @Override
        public String readData() {
            String data = wrapped.readData();
            return decrypt(data);
        }

        private String encrypt(String data) {
            return "encrypted:" + data;
        }

        private String decrypt(String data) {
            return data.replace("encrypted:", "");
        }
        
    }

    static class CompressionDecorator implements DataSource{

        private DataSource wrapped;

        public CompressionDecorator(DataSource dataSource){
            this.wrapped = dataSource;
        }

        @Override
        public void writeData(String data) {
            String compressed = compress(data);
            wrapped.writeData(compressed);
        }

        @Override
        public String readData() {
            String data = wrapped.readData();
            return decompress(data);
        }

        private String compress(String data){
            return "Compressed: "+data;
        }

        private String decompress(String data){
            return data.replace("Compressed: ", "");
        }
    }

    public static void main(String[] args) {
        DataSource ds = new FileDataSource("data.txt");
        ds = new EncryptionDecorator(ds);
        ds = new CompressionDecorator(ds);
        ds.writeData("This is the data");

        // Data gets compressed, then encrypted and then written to file
    }

}
