package software.ulpgc.kata4;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ImportCommand implements Command{

    public ImportCommand() {
    }

    @Override
    public void execute() {
        try {
            File input = getInputFile();
            File output = getOutputFile();
            doExecute(input, output);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void doExecute(File input, File output) throws Exception {
        Map<String, Integer> genresCounts = new HashMap<>();

        try(MovieReader reader = createMovieReader(input);
            MovieWriter writer = createMovieWriter(output)){
            while(true){
                Movie movie = reader.read();
                if(movie == null){
                    break;
                }
                writer.write(movie);

                for (String genre: movie.genres()){
                    genresCounts.put(genre, genresCounts.getOrDefault(genre, 0) + 1);
                }
            }
        }
        genresCounts.forEach((genre, count) -> System.out.println(genre + " " + count));
    }

    private static DataBaseMovieWriter createMovieWriter(File file) throws SQLException {
        return new DataBaseMovieWriter(deleteIfExists(file));
    }

    private static File deleteIfExists(File file) {
        if (file.exists()){
            file.delete();
        }
        return file;
    }


    private static FileMovieReader createMovieReader(File file) throws IOException {
        return new FileMovieReader(file, new TsvMovieDeserializer());
    }

    private File getInputFile() {
        return new File("C:/Users/maria/Desktop/María Universidad/IS2/title.basics.tsv.gz");
    }

    private File getOutputFile() {
        return new File("movies.db");
    }


}
