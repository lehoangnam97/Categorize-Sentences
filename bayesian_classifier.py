from category import  Category


def get_words(sentence):
    return sentence.split()



class BayesianTextClassifier:
    def __init__(self):
        self._categories = {}
        self._stopWords = []


    def add_stop_words(self, stopWord):
        self._stopWords.append(stopWord)

    # Phong: doc cau, xoa dau cau, xoa stop words
    @staticmethod
    def preprocess_sentence(sentence):
        return 0

    def add_sentence(self, sentence, categoryName):
        BayesianTextClassifier.preprocess_sentence(sentence)

        category = self._categories.get(categoryName, default = None)

        if category is None:
            category = Category(categoryName)
            category.add_words(get_words(sentence))
            self._categories[categoryName] = category
        else:
            category.add_words(get_words(sentence))

    # Nam: tinh toan va tra ve category (luu lai cac bien can tinh toan chu dung tinh lai)
    def classify(self, sentence):
        return ""
