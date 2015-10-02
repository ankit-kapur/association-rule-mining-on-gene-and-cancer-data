Association rule mining on gene and cancer data
-----------------------------------------------

How to run:
----------

1.	To configure, open the Configurations.java file. 
	Here you can configure the following
		- Minimum support [SUPPORT]
		- Minimum confidence [CONFIDENCE]
		- File path for the data [FILE_PATH]
		- Queries for testing the templates [TEMPLATE_TEST_QUERIES]

2.	To run, execute the main method in the Main class.

3.	To execute text-queries directly, please call the 
	parseTemplate function in the TemplateParser class.
	It takes in a text query and association rules and 
	gives out rules that satisfy the template.

