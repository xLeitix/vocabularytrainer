class ComparisonResult {

    private static  BASH_RED = "\033[31m"
    private static BASH_DEFAULT = "\033[0m"

    def expected
    def actual

    def isCorrect() { expected.equalsIgnoreCase(actual) }

    @Override
    public String toString() {
        def idx = indexOfDifference(expected, actual)
        def tmp = expected.toCharArray() as List
        tmp = tmp.plus(idx, BASH_RED)
        def inverted1 = (expected.toCharArray() as List).reverse()
        def inverted2 = (actual.toCharArray() as List).reverse()
        def idx2 = indexOfDifference(inverted1.join(), inverted2.join())
        if(idx2 > -1)
            tmp = tmp.plus(idx2, BASH_DEFAULT)
        tmp << BASH_DEFAULT
        return tmp.join()
    }

    private int indexOfDifference(String str1, String str2) {
        if (str1 == str2) {
            return -1;
        }
        if (str1 == null || str2 == null) {
            return 0;
        }
        int i;
        for (i = 0; i < str1.length() && i < str2.length(); ++i) {
            if (str1.charAt(i) != str2.charAt(i)) {
                break;
            }
        }
        if (i < str2.length() || i < str1.length()) {
            return i;
        }
        return -1;
    }

}
