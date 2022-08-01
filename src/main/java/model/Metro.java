package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.LinkedHashMap;
import java.util.List;
@Data
@AllArgsConstructor
public class Metro {
    private LinkedHashMap stations;
    private List lines;
    private List<List<Connection>> connections;




    }

