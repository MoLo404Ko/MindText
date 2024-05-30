import argparse
from transformers import GPT2Config, GPT2Tokenizer, GPT2LMHeadModel
import math
import torch
tokenizer = GPT2Tokenizer.from_pretrained("AlexWortega/instruct_rugptMedium")
gpt2_medium = GPT2LMHeadModel.from_pretrained("AlexWortega/instruct_rugptMedium")

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
gpt2_medium.to(device)

def calculate_perplexity(sentence, model, tokenizer):
    sentence_positive = 'довольна:'+sentence               
    sentence_negative = 'недовольна:'+sentence               
    list_sent = [sentence_positive, sentence_negative]
    ppl_values = []

    for sentence in list_sent:
        encodings = tokenizer(sentence, return_tensors='pt')
        input_ids = encodings.input_ids.to(device)
        with torch.no_grad():
            outputs = model(input_ids=input_ids, labels=input_ids)
        loss = outputs.loss
        ppl = math.exp(loss.item() * input_ids.size(1))
        ppl_values.append(ppl)
    
    if ppl_values[0] > ppl_values[1]:
        return 'отрицательный'
    elif ppl_values[0] < ppl_values[1]:
        return 'положительный'

def main():
    parser = argparse.ArgumentParser(description='')
    parser.add_argument('topic', type=str, help='')
    args = parser.parse_args()
    sentiment = calculate_perplexity(args.topic, gpt2_medium, tokenizer)
    print("Тональность текста:", sentiment)

if __name__ == "__main__":
    main()
