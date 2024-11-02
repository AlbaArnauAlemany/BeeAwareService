package ch.unil.doplab.beeaware.domain;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Credentials implements Serializable {

    private String username;
    private String password;
}