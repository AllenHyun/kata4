package software.ulpgc.kata4.model;

import java.util.List;

public record Movie (String id, String title, int year, int duration, List<String> genres){
}
