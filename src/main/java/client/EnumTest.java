package client;


/**
 * Created by Fabrice Vanner on 28.11.2016.
 */
public class EnumTest {
    public static enum TestEnum {
        ONE, TWO, THREE;
    }
    protected static <E extends Enum<E>> void enumValues(Class<E> enumData) {
        for (Enum<E> enumVal : enumData.getEnumConstants()) {
            System.out.println(enumVal.toString());
        }
    }



    public static void main(String param [] ) {
        EnumTest.enumValues(EnumTest.TestEnum.class);
    }
}
