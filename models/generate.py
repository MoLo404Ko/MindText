import argparse
from transformers import pipeline, GPT2Config

config = GPT2Config.from_pretrained("AlexWortega/instruct_rugptMedium")

# Установка pad_token_id для токена пробела
config.pad_token_id = config.eos_token_id

pipe = pipeline(model='AlexWortega/instruct_rugptMedium', config=config)

def main():
    parser = argparse.ArgumentParser(description='Generate text based on a given topic and length.')
    parser.add_argument('topic', type=str, help='Topic for text generation')
    parser.add_argument('length', type=int, choices=[1, 2, 3], help='Length of generated text: 1 for 10-20 words, 2 for 25-50 words, 3 for 30-60 words')
    args = parser.parse_args()

    if args.length == 1:
        config.max_length = 100
        config.min_length = 70
    elif args.length == 2:
        config.max_length = 600
        config.min_length = 300
    else:
        config.max_length = 1000
        config.min_length = 600

    # Использование слов вместо символов
    topic_words = args.topic.split()
    output = pipe(" ".join(topic_words), max_length=config.max_length, min_length=config.min_length, do_sample=True)[0]['generated_text']
    print(output.replace(" ".join(topic_words), "").strip())

if __name__ == "__main__":
    main()
