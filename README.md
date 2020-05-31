# Genome assembly project for Norway spruce (Computer science for mathematicians VT20/HT18)
> The goal of this project is to characterize a given graph. The graph comes from a real dataset
> from the Spruce genome assembly project which aims at inferring the genome of Norway spruce.
> In genome assembly, graphs are used to represent information about contigs, DNA segments, 
> and how they overlap. 
> The nodes, or vertices, of the graph are the DNA segments (contigs). An edge between two nodes
> means that those two segments overlap, meaning they should potentially be merged into a larger
> segment when the genome is assembled. The graph is undirected by nature, since an overlap between
> two DNA segments doesn't encode a 'direction'.
> The main difficulty of detecting overlaps is due to the fact that the Norway spruce genome is 
> very repetitive and has low complexity. This results in many edges, most of which don't provide
> useful information, and the number of edges is so large that standard genome assembly tools 
> have difficulties processing the data.
> For this project, the goal is to find the distribution of the node degrees, the number of 
> connected components of the graph, and the distribution of the component sizes.

## Table of contents
* [Technologies](#technologies)
* [Description](#description)

## Technologies
* Java (openJDK 14)

## Description
Before any code is run, the original data file was split into smaller parts. 
The original file contained approximately 64 million lines (data points).
This was split into 641 chunks/parts of 100 000 lines each. This is assumed by the code.

Most of the detailed information of the classes and their methods can be found in docstrings.
I will therefore restrict this description to an overview of how they work together.

LineParser.java and LineParserParallel.java handles IO processing of the data chunks, and contain
methods for things such as filtering out false overlaps (instances where one contig is contained 
within another and therefore not a relevant data point), indexing contig identifiers, and generating
a graph representation of the data. 
The parallel line parser utilizes a thread pool to perform concurrent processing where it is 
applicable, for example when filtering out false overlaps. Some processing, such as indexing the
contig identifiers (i.e. mapping each unique contig string identifier to a unique integer for the
internal graph representation) is more difficult to do with parallel processing and is handled 
by the regular line parser.

Graph.java contains an adjacency list implementation of an undirected graph, and is used to represent
the data as a graph data structure. It contains methods for finding degree distributions, connected
components and such.

CustomWriter.java is a small utility class that contains some methods for printing information to the
console and writing results to text files for future use (e.g. making charts and such).

Main.java is where the things are put together in order to create a graph representation of the data
and find its degree distribution and component distribution.

A rough timeline is as follows:

* Split data into manageable parts (before any code is run)
* Filter false overlaps 
* Index the contig string identifiers to integers
* Generate a graph representation of the filtered data using the integer indexing 
* Do some computation on the graph (degree distribution, components)

Most information and details are found in the various docstrings.
