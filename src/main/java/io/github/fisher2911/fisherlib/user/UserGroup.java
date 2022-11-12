package io.github.fisher2911.fisherlib.user;

import java.util.Collection;

public interface UserGroup<T extends CoreUser> {

    Collection<T> getUsers();

}
