from bayesian_classifier import BayesianTextClassifier

training_set = {}
test_set = {}


# Nam: doc het tat ca sentence, them 80% dau tien vao training_set va 20% con lai vao test_set
def read_sentences_from_file(file):
    return 0


# Phong: doc het tat ca stop words va tra ve danh sach stop words
def read_stop_words_from_file(file):
    return []


classifier = BayesianTextClassifier()

for stopword in read_stop_words_from_file("abc.txt"):
    classifier.add_stop_words(stopword)

read_sentences_from_file("abc.txt", classifier=classifier)

# Kien: viet test cho 20% con lai dung ham classifier.classify() de kiem tra do chinh xac:
# lay so truong hop chinh xac chia cho tong so truong hop, in ra do chinh xac
