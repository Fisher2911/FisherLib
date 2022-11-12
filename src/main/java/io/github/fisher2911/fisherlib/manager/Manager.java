package io.github.fisher2911.fisherlib.manager;

import java.util.Optional;

public interface Manager<T, ID> {

    Optional<T> get(ID id);

    T forceGet(ID id);

}
