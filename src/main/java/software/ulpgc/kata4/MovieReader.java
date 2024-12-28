package software.ulpgc.kata4;

import java.io.IOException;

public interface MovieReader extends AutoCloseable{
    Movie read() throws IOException;
}
