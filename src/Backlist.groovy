class Backlist {

    private list = [:]

    def isEmpty() { list.size() == 0 }

    def onList(def q) { list.containsKey(q) }

    def removeFromList(def q) {
        list.remove(q)
    }

    def addToList(def question){
        list[question.q] = question
    }

    def randomEntry(def random) {
        def n = list.size()
        def key = list.keySet()[random.nextInt(n)]
        return list[key]
    }

}
