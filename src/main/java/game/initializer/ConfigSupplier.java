package game.initializer;

import java.util.List;

public interface ConfigSupplier {

    List<String> get(String... keys);
}
