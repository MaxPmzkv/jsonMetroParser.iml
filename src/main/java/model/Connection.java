package model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class Connection {
    private String line;
    private String station;
}
