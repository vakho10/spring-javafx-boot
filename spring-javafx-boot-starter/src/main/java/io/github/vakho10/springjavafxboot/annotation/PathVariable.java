package io.github.vakho10.springjavafxboot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a parameter in an {@link FxMapping} handler method for injection from
 * a path variable.
 * <p>
 * Path variables are declared in the route path using {@code {name}} syntax
 * and extracted when the route is matched:
 *
 * <pre>{@code
 * @FxMapping(value = "/users/{id}", parent = "/")
 * public String userDetail(@PathVariable("id") Long id, FxModel model) {
 *     model.put("user", userService.findById(id));
 *     return "user-detail";
 * }
 * }</pre>
 *
 * Navigation:
 * <pre>{@code
 * router.navigateTo("/users/42");
 * }</pre>
 *
 * Supported types: {@code String}, {@code Integer}/{@code int}, {@code Long}/{@code long},
 * {@code Double}/{@code double}, {@code Boolean}/{@code boolean}.
 *
 * @see FxMapping
 * @see io.github.vakho10.springjavafxboot.router.FxRouter
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PathVariable {

    /**
     * The name of the path variable. Must match a {@code {name}} placeholder
     * in the {@link FxMapping#value()}.
     */
    String value();
}
