package example.tax;

/** 車両。displacement=排気量(cc), ageYears=初度登録からの経過年数, reduced=減免対象か */
public record Vehicle(String id, int displacement, int ageYears, boolean reduced) {
}
