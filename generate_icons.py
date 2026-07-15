from PIL import Image, ImageDraw, ImageFont

def create_icon(size, filename):
    # Create a blue background
    image = Image.new('RGB', (size, size), color='#2196F3')
    draw = ImageDraw.Draw(image)

    # Draw a white circle
    margin = size // 10
    draw.ellipse([margin, margin, size - margin, size - margin], fill='white')

    # Draw a blue "P"
    # Note: Using default font if others are not available
    try:
        font = ImageFont.truetype("arial.ttf", size // 2)
    except:
        font = ImageFont.load_default()

    text = "P"
    # Calculate text position (rough estimate)
    draw.text((size // 2.8, size // 4), text, fill='#2196F3', font=font)

    image.save(filename)
    print(f"Saved {filename}")

create_icon(192, "app/src/wasmJsMain/resources/icon-192.png")
create_icon(512, "app/src/wasmJsMain/resources/icon-512.png")
