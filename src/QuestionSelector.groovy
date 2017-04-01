class QuestionSelector {

    private static SELECT_FROM_BACK = 0.35
    private static START_SCORE = 3

    private def db
    private def backlist
    private Random r = new Random()

    def selectQuestion() {

        if(!backlist.isEmpty() && r.nextFloat() < SELECT_FROM_BACK) {
            backlist.randomEntry(r)
        } else {
            roulettewheelSelectFromDB(db, r)
        }

    }

    private def roulettewheelSelectFromDB(def db, def random) {

        def allWords = db.keySet()
        def scoreList = allWords.collect{
            def baseScore = db[it].incorrect - db[it].correct + START_SCORE
            if(baseScore < 1)
                baseScore = 1
            return baseScore
        }
        def maxScore = scoreList.sum()
        def sample = random.nextInt(maxScore)
        for(int i=0; i<allWords.size(); i++) {
            def w = allWords[i]
            def s = scoreList[i]
            sample -= s
            if(sample <=0)
                return db[w]
        }

    }

}
