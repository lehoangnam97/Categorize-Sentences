class Category:

    def __init__(self, categoryName):
        self._categoryName = categoryName
        # dictionary: key la word, value la so lan xuat hien
        self._words = {}

    def add_words(self, word):
        self._words.append(word)

