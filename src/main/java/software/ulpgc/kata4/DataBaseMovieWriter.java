package software.ulpgc.kata4;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.sql.*;
import java.util.List;

public class DataBaseMovieWriter implements MovieWriter, AutoCloseable {
    private final Connection connection;
    private final PreparedStatement insertStatement;
    private final static String CreateTableStatement = """
            CREATE TABLE IF NOT EXISTS movies (
                id TEXT PRIMARY KEY,
                title TEXT NOT NULL,
                year INTEGER,
                duration INTEGER
            )
            """;

    private final static String InsertRecordStatement = """
            INSERT INTO movies (id, title, year, duration)
            VALUES (?, ?, ?, ?)
            """;

    public DataBaseMovieWriter(File file) throws SQLException {
        this(connectionFor(file));
    }

    public DataBaseMovieWriter(Connection connection) throws SQLException{
        this.connection = connection;
        this.connection.setAutoCommit(false);
        this.insertStatement = initDatabase(this.connection);
    }

    private PreparedStatement initDatabase(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        Statement.execute(CreateTableStatement);
        return connection.prepareStatement(InsertRecordStatement);
    }

    private static String connectionFor(File file){
        return "jdbc:sqlite:" + file.getAbsolutePath();
    }

    @Override
    public void write(Movie movie) throws IOException {
        try {
            updateInsertStatementWith(movie);
            insertStatement.execute();
        } catch (IOException e){
            throw new IOException(e.getMessage());
        }
    }

    private void updateInsertStatementWith(Movie movie)throws SQLException{
        for(Parameter parameter: toParameters(movie)){
            updateInsertStatementWith(parameter);
        }
    }

    private void updateInsertStatement(Parameter parameter) throws SQLException{
        if(isNull(parameter.value)){
            insertStatement.setNull(parameter.id, parameter.type);
        }
        else{
            insertStatement.setObject(parameter.id, parameter.value);
        }
    }

    private boolean isNull(Object value){
        return value instanceof  Integer && (Integer) value == -1;
    }

    private List<Parameter> toParameters(Movie movie){
        return List.of(
                new Parameter(1, movie.id(), Types.LONGNVARCHAR),
                new Parameter(2, movie.title(), Types.LONGNVARCHAR),
                new Parameter(3, movie.year(), Types.INTEGER),
                new Parameter(4, movie.duration(), Types.INTEGER)
        );
    }

    private record Parameter(int id, Object value, int type){

    }

    @Override
    public void close() throws Exception {
        this.connection.close();
    }
}
