package software.ulpgc.kata4;

import java.io.IOException;

public interface MovieWriter extends AutoCloseable{
    void write(Movie movie) throws IOException;
}
