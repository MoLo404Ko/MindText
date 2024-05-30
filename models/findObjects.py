import argparse
from natasha import Segmenter, MorphVocab, NewsEmbedding, NewsMorphTagger, NewsSyntaxParser, NewsNERTagger, NamesExtractor, Doc
import spacy
from spacy import displacy

def extract_entities(text, entity_type):
    segmenter = Segmenter()
    morph_vocab = MorphVocab()
    embedding = NewsEmbedding()
    morph_tagger = NewsMorphTagger(embedding)
    syntax_parser = NewsSyntaxParser(embedding)
    ner_tagger = NewsNERTagger(embedding)
    names_extractor = NamesExtractor(morph_vocab)
    doc = Doc(text)
    doc.segment(segmenter)
    doc.tag_morph(morph_tagger)
    for token in doc.tokens:
        token.lemmatize(morph_vocab)
    doc.tag_ner(ner_tagger)
    for span in doc.spans:
        span.normalize(morph_vocab)
    rus_model = spacy.load("ru_core_news_sm")
    rus_doc = rus_model(text)
    names = []
    locations = []
    organizations = []
    for ent in rus_doc.ents:
        if ent.label_ == 'PER':
            names.append(ent.text)
        elif ent.label_ == 'LOC':
            locations.append(ent.text)
        elif ent.label_ == 'ORG':
            organizations.append(ent.text)
        
    if entity_type.lower() == 'имена':
        print("Имена: ")
        for name in names:
            print(name, " ")
    elif entity_type.lower() == 'компании':
        print("Организации:")
        for organization in organizations:
            print(organization, " ")
    elif entity_type.lower() == 'географические названия':
        print("Географические названия:")
        for location in locations:
            print(location, " ")
    else:
        print("Сущностей данного типа в тексте нет.")

def main():
    parser = argparse.ArgumentParser(description="Извлечение именованных сущностей из текста.")
    parser.add_argument("text", type=str, help="Текст для анализа.")
    parser.add_argument("entity_type", type=str, help="Тип сущностей для извлечения (имена, клички, компании, географические названия).")
    args = parser.parse_args()
    extract_entities(args.text, args.entity_type)


if __name__ == "__main__":
    main()
