package plugin;


public interface LarkService {


    void start(String userDescription);

    void success(String userDescription);

    void failed();
    
    void abort();
}
