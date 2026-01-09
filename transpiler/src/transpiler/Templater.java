package transpiler;
import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class Templater {

    List<String> chunks;
    Map<Integer, String> placeholders;

    private String getTemplate(String name) throws IOException {
        String path = String.format("templates/%s.tpl", name);
        try (InputStream in = getClass().getResourceAsStream(path)) {
            if (in == null) {
                throw new RuntimeException("TEMPLATER: missing template");
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public Templater(String name) throws IOException {
        chunks = new ArrayList<>();
        placeholders = new HashMap<>();
        String template = getTemplate(name);
        StringBuilder builder = new StringBuilder();

        boolean inPlaceholder = false;
        for (int x = 0; x < template.length(); x++) {
            char c = template.charAt(x);

            if (c == '@') {
                String chunk = builder.toString();
                builder.setLength(0);

                if (inPlaceholder) {
                    placeholders.put(chunks.size(), chunk);
                    chunks.add(null);
                }
                else {
                    chunks.add(chunk);
                }

                inPlaceholder = !inPlaceholder;
            }
            else {
                builder.append(c);
            }
        }

        chunks.add(builder.toString());
    }

    public String generate(Map<String, String> fillers) {
        StringBuilder builder = new StringBuilder();
        for (int x = 0; x < chunks.size() ; x++) {
            String chunk = chunks.get(x);
            if (chunk == null) {
                String placeholder = placeholders.get(x);
                String filler = fillers.get(placeholder);
                if (filler == null) {
                    throw new RuntimeException(String.format(
                            "TEMPLATER: unknown filler => %s", placeholder
                    ));
                }
                builder.append(filler);
            }
            else {
                builder.append(chunk);
            }
        }
        return builder.toString();
    }
}
