package sae.semestre.six.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class FileInitializer {
    public static final String BILL_FOLDER = "C:\\hospital\\bill\\";

    @PostConstruct
    public void init() {
        File folder = new File(BILL_FOLDER);

        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (!created) {
                throw new IllegalStateException("Échec de la création du dossier : " + BILL_FOLDER);
            }
        }
    }
}