#!/bin/bash

DIR="$PWD"

if [[ $(lsb_release -is 2>/dev/null) == "Ubuntu" ]]; then
    sudo apt install texlive texlive-latex-extra texlive-fonts-recommended -y
else
    echo "skipping installation of deps for non-Ubuntu OS"
fi

MDFILE="$DIR/docs/overview.md"

# Define the output PDF file name
OUTPUT_PDF="MongoDB_JDBC_Guide.pdf"

# Use pandoc to convert the markdown file to a PDF
pandoc -f gfm -V geometry:a4paper -V geometry:margin=2cm --toc -s -o "$DIR/docs/$OUTPUT_PDF" $MDFILE

# Inform the user of the output file
echo "PDF generated: $OUTPUT_PDF"
