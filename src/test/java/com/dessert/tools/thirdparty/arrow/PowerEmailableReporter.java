package com.dessert.tools.thirdparty.arrow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.IInvokedMethod;
import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestClass;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.collections.Lists;
import org.testng.internal.Utils;
import org.testng.log4testng.Logger;
import org.testng.xml.XmlSuite;

import com.dessert.tools.thirdparty.arrow.utils.ConfigReader;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;

/**
 * Reported designed to render self-contained HTML top down view of a testing
 * suite.
 * 
 * @author renta
 * @since 5.2
 * @version $Revision: 719 $
 */
public class PowerEmailableReporter implements IReporter {
	private static final Logger L = Logger.getLogger(PowerEmailableReporter.class);

	// ~ Instance fields ------------------------------------------------------

	private PrintWriter m_out;

	private int m_row;  //表的当前行数

	private Integer m_testIndex;    //表第一列各行链接到方法信息表下的#id，用在 <a href='#id'>

	private Set<Integer> testIds = new HashSet<Integer>();     //所有方法的id
	private List<Integer> allRunTestIds = new ArrayList<Integer>();     //所有被执行的test的id
	private JavaDocBuilder builder = new JavaDocBuilder();

	// ~ Methods --------------------------------------------------------------

	/** Creates summary of the run */
	public void generateReport(List<XmlSuite> xml, List<ISuite> suites, String outdir) {
		try {
			m_out = createWriter(outdir);
		} catch (IOException e) {
			L.error("output file", e);
			return;
		}
		ConfigReader cr = ConfigReader.getInstance();
		builder.setEncoding(cr.getSrouceCodeEncoding());
		builder.addSourceTree(new File(cr.getSourceCodeDir()));
		startHtml(m_out);
		generateSuiteSummaryReport(suites);
		testIds.clear();  //清理一次id
		generateMethodSummaryReport(suites);
		testIds.clear();
		generateMethodDetailReport(suites);
		testIds.clear();
		endHtml(m_out);
		m_out.flush();
		m_out.close();
	}

	protected PrintWriter createWriter(String outdir) throws IOException {
		new File(outdir).mkdirs();
		return new PrintWriter(new BufferedWriter(new FileWriter(new File(outdir, "power-emailable-report.html"))));
	}

	/**
	 * Creates a table showing the highlights of each test method with links to the method details <p>
	 * 创建显示各个方法信息概览表
	 * 
	 */
	protected void generateMethodSummaryReport(List<ISuite> suites) {
		startResultSummaryTable("methodOverview");    //创建方法信息表头
		int testIndex = 1;
		for (ISuite suite : suites) {
			if (suites.size() > 1) {
				titleRow(suite.getName(), 5);     //如果有多个suites,则标题行，横跨5列
			}
			
			Map<String, ISuiteResult> r = suite.getResults();
			for (ISuiteResult sr : r.values()) {
				ITestContext testContext = sr.getTestContext();
				String testName = testContext.getName();    //得到<test name = 'xxx'中的xxx值
				m_testIndex = testIndex;

				resultSummary(suite, testContext.getSkippedConfigurations(), testName, "skipped", " (configuration methods)");
				resultSummary(suite, testContext.getSkippedTests(), testName, "skipped", "");
				resultSummary(suite, testContext.getFailedConfigurations(), testName, "failed", " (configuration methods)");
				resultSummary(suite, testContext.getFailedTests(), testName, "failed", "");               //目前主要是下面这两个场景
				resultSummary(suite, testContext.getPassedTests(), testName, "passed", "");

				testIndex++;
			}
		}
		m_out.println("</table>");
	}

	/** Creates a section showing known results for each method */
	protected void generateMethodDetailReport(List<ISuite> suites) {
		for (ISuite suite : suites) {
			Map<String, ISuiteResult> r = suite.getResults();
			for (ISuiteResult r2 : r.values()) {
				ITestContext testContext = r2.getTestContext();
				if (r.values().size() > 0) {
					m_out.println("<hr/>");
					m_out.println("<h3>" + testContext.getName() + "</h3>");
				}
				resultDetail(testContext.getFailedConfigurations());
				resultDetail(testContext.getFailedTests());
				resultDetail(testContext.getSkippedConfigurations());
				resultDetail(testContext.getSkippedTests());
				resultDetail(testContext.getPassedTests());
			}
		}
	}

	/**
	 * 
	 * Ex：resultSummary(suite, testContext.getFailedConfigurations(), testName, "failed", " (configuration methods)");
	 * <br/> resultSummary(suite, testContext.getFailedTests(), testName, "failed", "");
	 * 
	 * @param tests
	 */
	private void resultSummary(ISuite suite, IResultMap tests, String testname, String style, String details) {
		if (tests.getAllResults().size() > 0) {
			StringBuffer buff = new StringBuffer();
			DecimalFormat numFormat=new DecimalFormat("#.##"); 
			String lastClassName = "";
			int mq = 0;   					  //表的标题行
			int cq = 0;  					 //表的内容行
			Map<String, Integer> methods = new HashMap<String, Integer>();
			Set<String> setMethods = new HashSet<String>();
			for (ITestNGMethod method : getMethodSet(tests, suite)) {
				m_row += 1;
				
				ITestClass testClass = method.getTestClass();
				String className = testClass.getName();
				if (mq == 0) {                                           //先是标题行
					String id = (m_testIndex == null ? null : "t" + Integer.toString(m_testIndex));
//					titleRow(testname + " &#8212; " + style + details, 5, id);
					titleRowTextLeft(testname + " &#8212; " + style + details, 5, id);
					m_testIndex = null;
				}
				
				if (!className.equalsIgnoreCase(lastClassName)) {
					if (mq > 0) {
						cq += 1;
						m_out.print("<tr class=\"" + style + (cq % 2 == 0 ? "even" : "odd") + "\">" + "<td");  //根据奇偶来拼接style, 如failedodd或failedeven
						if (mq > 1) {
							m_out.print(" rowspan=\"" + mq + "\"");
						}
//						String tempName = lastClassName.substring(lastClassName.lastIndexOf(".")+1);      //只打印类名，去掉路径
						m_out.println(">" + lastClassName + "</td>" + buff);
					}
					mq = 0;
					buff.setLength(0);
					lastClassName = className;
				}
				Set<ITestResult> resultSet = tests.getResults(method);
				long end = Long.MIN_VALUE;
				long start = Long.MAX_VALUE;
				for (ITestResult testResult : tests.getResults(method)) {
					if (testResult.getEndMillis() > end) {
						end = testResult.getEndMillis();
					}
					if (testResult.getStartMillis() < start) {
						start = testResult.getStartMillis();
					}
				}
				mq += 1;
				if (mq > 1) {
					buff.append("<tr class=\"" + style + (cq % 2 == 0 ? "odd" : "even") + "\">");
				}
				String description = method.getDescription();
				String testInstanceName = resultSet.toArray(new ITestResult[] {})[0].getTestName();
				
				// Calculate each test run times, the result shown in the html report.
				ITestResult[] results = resultSet.toArray(new ITestResult[] {});
				String methodName = method.getMethodName();  //获取方法名
				if (setMethods.contains(methodName)) {
					methods.put(methodName, methods.get(methodName) + 1);   //如果方法名相同，则此方法使用次数+1
				} else {
					setMethods.add(methodName);
					methods.put(methodName, 0);
				}
				String parameterString = "";
				int count = 0;

				ITestResult result = null;
				if (results.length > methods.get(methodName)) {
					result = results[methods.get(methodName)];
					int testId = getId(result);

					for (Integer id : allRunTestIds) {
						if (id.intValue() == testId)
							count++;
					}
					Object[] parameters = result.getParameters();

					boolean hasParameters = parameters != null && parameters.length > 0;
					if (hasParameters) {
						for (Object p : parameters) {
							parameterString = parameterString + Utils.escapeHtml(p.toString()) + " ";
						}
					}
				}

				int methodId = method.getTestClass().getName().hashCode();
				methodId = methodId + method.getMethodName().hashCode();
				if (result != null)
					methodId = methodId + (result.getParameters() != null ? Arrays.hashCode(result.getParameters()) : 0);
				//下面涉及到添加 方法名，
//				buff.append("<td><a href=\"#m" + methodId + "\">" + qualifiedName(method) + " " + (description != null && description.length() > 0 ? "(\"" + description + "\")" : "") + "</a>" + (null == testInstanceName ? "" : "<br>(" + testInstanceName + ")") + "</td><td>" + this.getAuthors(className, method) + "</td><td class=\"numi\">" + resultSet.size() + "</td>" + "<td>" + (count == 0 ? "" : count) + "</td>" + "<td>" + parameterString + "</td>" + "<td>" + start + "</td>" + "<td class=\"numi\">" + (end - start) + "</td>" + "</tr>");
				buff.append("<td><a href=\"#m" + methodId + "\">" + qualifiedName(method) +  "</a>" + (null == testInstanceName ? "" : "<br>(" + testInstanceName + ")") + "</td><td>" + (description != null && description.length() > 0 ? description : "") + "</td>" + "<td>" + (count == 0 ? "" : count) + "</td>" + "<td class=\"numi\">" + numFormat.format((double)(end-start)/1000) + "</td>" + "</tr>");
			}
			
			if (mq > 0) {
				cq += 1;
				m_out.print("<tr class=\"" + style + (cq % 2 == 0 ? "even" : "odd") + "\">" + "<td");
				if (mq > 1) {
					m_out.print(" rowspan=\"" + mq + "\"");    //TBC
				}
				m_out.println(">" + lastClassName + "</td>" + buff);
			}
		}
	}

	
	/** 开始并定义结果概览表表格的各列  */
	private void startResultSummaryTable(String style) {
		m_out.println("<h2>测试用例</h2>");
		tableStart(style, "summary");
		m_out.println("<tr><th>Class Name</th><th>Method Name</th><th>Description</th><th>Running<br/>Counts</th><th>Time (s)</th></tr>");
		m_row = 0;
	}

	/**
	 * 如果方法属于group，则加上group信息；否则，将方法名加粗 <p>
	 * 
	 * @param method
	 * @return
	 */
	private String qualifiedName(ITestNGMethod method) {
		StringBuilder addon = new StringBuilder();
		String[] groups = method.getGroups();
		int length = groups.length;
		if (length > 0 && !"basic".equalsIgnoreCase(groups[0])) {  //如果方法输入group
			addon.append("(");
			for (int i = 0; i < length; i++) {
				if (i > 0) {
					addon.append(", ");
				}
				addon.append(groups[i]);
			}
			addon.append(")");
		}

		return "<b>" + method.getMethodName() + "</b> " + addon;     //对方法名加强， 如果没有group信息，则只是加粗方法名
	}

	/**
	 * 结果详细信息
	 * 
	 * @param tests
	 */
	private void resultDetail(IResultMap tests) {
		for (ITestResult result : tests.getAllResults()) {
			ITestNGMethod method = result.getMethod();

			int methodId = getId(result);

			String cname = method.getTestClass().getName();     //用例完整名+方法名
			m_out.println("<h4 id=\"m" + methodId + "\" name=\"m" + methodId + "\" >" + cname + " : " + method.getMethodName() + "</h4>");
			Set<ITestResult> resultSet = tests.getResults(method);
			generateForResult(result, method, resultSet.size());
			m_out.println("<p class=\"totop\" style=\"font-size:1.0em;\"><a href=\"#summary\">Back To Summary</a></p>");

		}
	}

	private void generateForResult(ITestResult ans, ITestNGMethod method, int resultSetSize) {
		Object[] parameters = ans.getParameters();      //获取此test result涉及的所有参数
		boolean hasParameters = parameters != null && parameters.length > 0;
		if (hasParameters) {
			tableStart("result", null);
			m_out.print("<tr class=\"param\" style=\"font-size:0.9em;\">");
			for (int x = 1; x <= parameters.length; x++) {
				m_out.print("<th>Parameter #" + x + "</th>");
			}
			m_out.println("</tr>");
			m_out.print("<tr class=\"param stripe\" style=\"font-size:0.9em;\">");
			for (Object p : parameters) {
				m_out.println("<td>" + Utils.escapeHtml(p.toString()) + "</td>");
			}
			m_out.println("</tr>");
		}
		//下面添加其他信息，如截图等
		List<String> msgs = Reporter.getOutput(ans);
		boolean hasReporterOutput = msgs.size() > 0;
		Throwable exception = ans.getThrowable();  //异常信息
		boolean hasThrowable = exception != null;
		if (hasReporterOutput || hasThrowable) {
			if (hasParameters) {                      //如果有参数，那么横跨参数列，并将信息显示在其内
				m_out.print("<tr><td");
				if (parameters.length > 1) {
					m_out.print(" colspan=\"" + parameters.length + "\"");
				}
				m_out.println(">");
			} else {
				m_out.println("<div>");  //没有参数就用div来显示
			}
			if (hasReporterOutput) {
				if (hasThrowable) {
					m_out.println("<h4>Test Messages</h4>");
				}
				for (String line : msgs) {
					m_out.println(line + "<br/>");
				}
			}
			if (hasThrowable) {
				boolean wantsMinimalOutput = ans.getStatus() == ITestResult.SUCCESS;
				if (hasReporterOutput) {
					m_out.println("<h4>" + (wantsMinimalOutput ? "Expected Exception" : "Failure") + "</h4>");
				}
				generateExceptionReport(exception, method);
			}
			if (hasParameters) {
				m_out.println("</td></tr>");
			} else {
				m_out.println("</div>");
			}
		}
		if (hasParameters) {
			m_out.println("</table>");
		}
	}

	protected void generateExceptionReport(Throwable exception, ITestNGMethod method) {
		m_out.print("<div class=\"stacktrace\">");
		m_out.print(Utils.stackTrace(exception, true)[0]);
		m_out.println("</div>");
	}

	/**
	 * Since the methods will be sorted chronologically, we want to return the ITestNGMethod from the invoked methods. <p>
	 * 由于方法会被按照时间来排序，通过此从被调用的方法中获取 ITestNGMethod集合
	 * 
	 * Ex: getMethodSet(overview.getPassedTests(), suite).size();
	 * 
	 * @param 传入一组<test>下的用例
	 * 
	 */
	private Collection<ITestNGMethod> getMethodSet(IResultMap tests, ISuite suite) {
		List<IInvokedMethod> r = Lists.newArrayList();
		List<IInvokedMethod> invokedMethods = suite.getAllInvokedMethods();

		// 去掉重试的方法
		for (IInvokedMethod im : invokedMethods) {
			if (tests.getAllMethods().contains(im.getTestMethod())) {
				int testId = getId(im.getTestResult());
				if (!testIds.contains(testId)) {
					testIds.add(testId);
					r.add(im);
				}
			}
		}
		Arrays.sort(r.toArray(new IInvokedMethod[r.size()]), new TestSorter());  //根据调用时间来排序
		List<ITestNGMethod> result = Lists.newArrayList();

		// Add all the invoked methods
		for (IInvokedMethod m : r) {
			result.add(m.getTestMethod());
		}

		// Add all the methods that weren't invoked (e.g. skipped) that we
		// haven't added yet
		// for (ITestNGMethod m : tests.getAllMethods()) {
		// if (!result.contains(m)) {
		// result.add(m);
		// }
		// }

		for (ITestResult allResult : tests.getAllResults()) {
			int testId = getId(allResult);
			if (!testIds.contains(testId)) {
				result.add(allResult.getMethod());
			}
		}

		return result;
	}

	/**
	 * 生成最上面的汇总报告
	 * 
	 * @param suites
	 */
	public void generateSuiteSummaryReport(List<ISuite> suites) {
		m_out.println("	<h2>测试结果</h2>");
		tableStart("testOverview", null);
		m_out.print("<tr>");
		tableColumnStart("Tests");
//		tableColumnStart("Passed<br/>测试方法");
		tableColumnStart("Passed");
		tableColumnStart("Skipped");
		tableColumnStart("Failed");
		tableColumnStart("Time(s)");
//		tableColumnStart("包含的Groups");
//		tableColumnStart("排除的Groups");
		
//		tableColumnStart("Tests");
//		tableColumnStart("Methods<br/>Passed");
//		tableColumnStart("Scenarios<br/>Passed");
//		tableColumnStart("# skipped");
//		tableColumnStart("# failed");
//		tableColumnStart("Total<br/>Time");
//		tableColumnStart("Included<br/>Groups");
//		tableColumnStart("Excluded<br/>Groups");
		
		m_out.println("</tr>");              		 //表头结束
		NumberFormat formatter = new DecimalFormat("#,##0.0");
		int qty_tests = 0;
		int qty_pass_m = 0;               //通过的测试方法
		int qty_pass_s = 0;              //通过的测试
		int qty_skip = 0;
		int qty_fail = 0;
		long time_start = Long.MAX_VALUE;
		long time_end = Long.MIN_VALUE;
		m_testIndex = 1;               //行的索引
		for (ISuite suite : suites) {            //遍历所有的suite
			if (suites.size() > 1) {
				titleRow(suite.getName(), 5);              //在suite数目在2个以上时，会打印suite的名称，横跨5列
			}
			Map<String, ISuiteResult> tests = suite.getResults();
			for (ISuiteResult suiteResult : tests.values()) {
				qty_tests += 1;          //测试数量+1
				ITestContext overview = suiteResult.getTestContext();  //获得当前<test>测试上下文信息
				startSummaryRow(overview.getName());  //开始每一行第一列
				getAllTestIds(overview, suite);    //得到每一个<test>下的所有测试的唯一id
				int q =0;
				
//				q = getMethodSet(overview.getPassedTests(), suite).size();  //得到通过方法的数目
//				qty_pass_m += q; 
//				summaryCell(q, Integer.MAX_VALUE);  //生成第二列 pass测试方法
				
				q = overview.getPassedTests().size(); //test下通过的所有测试场景
				qty_pass_s += q;
				summaryCell(q, Integer.MAX_VALUE); //生成第三列pass场景

				q = getMethodSet(overview.getSkippedTests(), suite).size();
				qty_skip += q;
				summaryCell(q, 0);   //生成第四列skipped用例数

				q = getMethodSet(overview.getFailedTests(), suite).size();
				qty_fail += q;
				summaryCell(q, 0);  //生成第五列skipped用例数

				time_start = Math.min(overview.getStartDate().getTime(), time_start);
				time_end = Math.max(overview.getEndDate().getTime(), time_end);
				summaryCell(formatter.format((overview.getEndDate().getTime() - overview.getStartDate().getTime()) / 1000.), true);  ////生成第六列执行时间
//				summaryCell(overview.getIncludedGroups());
//				summaryCell(overview.getExcludedGroups());
				m_out.println("</tr>");
				m_testIndex++;
			}
		}
		
		//如果测试数量大于1条的情况
		if (qty_tests > 1) {
			m_out.println("<tr class=\"total\"><td>合计</td>");
//			summaryCell(qty_pass_m, Integer.MAX_VALUE);
			summaryCell(qty_pass_s, Integer.MAX_VALUE);
			summaryCell(qty_skip, 0);
			summaryCell(qty_fail, 0);
			summaryCell(formatter.format((time_end - time_start) / 1000.), true);
//			m_out.println("<td colspan=\"2\">&nbsp;</td></tr>");   //暂不使用group，略去这两列
		}
		m_out.println("</table>");
		m_out.println("<br/>");
	}

	@SuppressWarnings("unused")
	private void summaryCell(String[] val) {
		StringBuffer b = new StringBuffer();
		for (String v : val) {
			b.append(v + " ");
		}
		summaryCell(b.toString(), true);
	}

	/**
	 * summary中的一个单元，如果fail了，则class属性为numi_attn,背景标红
	 * 
	 * @param v 填入值
	 * @param isgood 
	 */
	private void summaryCell(String v, boolean isgood) {
		m_out.print("<td class=\"numi" + (isgood ? "" : "_attn") + "\">" + v + "</td>");
	}

	/**
	 * 开始每一行的第一列信息
	 * 
	 * @param label 
	 */
	private void startSummaryRow(String label) {
		m_row += 1;
		m_out.print("<tr" + (m_row % 2 == 0 ? " class=\"stripe\"" : "") + "><td style=\"text-align:left;padding-right:2em\"><a href=\"#t" + m_testIndex + "\">" + label + "</a>" + "</td>");
	}

	/**
	 * 填写总结表中的一个单元格
	 * 
	 * Ex：summaryCell(q, 0);
	 * 
	 * @param v
	 * @param maxexpected
	 */
	
	private void summaryCell(int v, int maxexpected) {
		summaryCell(String.valueOf(v), v <= maxexpected);
	}

	private void tableStart(String cssclass, String id) {
		m_out.println("<table cellspacing=\"0\" cellpadding=\"0\"" + (cssclass != null ? " class=\"" + cssclass + "\"" : " style=\"padding-bottom:2em\"") + (id != null ? " id=\"" + id + "\"" : "") + ">");
		m_row = 0;
	}

	private void tableColumnStart(String label) {
		m_out.print("<th>" + label + "</th>");
	}
 
	/**
	 * 不带id的表头行
	 *
	 * Ex: titleRow(suite.getName(), 8);   
	 * 
	 * @param label 标题名
	 * @param cq 表格横跨多少列
	 */
	private void titleRow(String label, int cq) {
		titleRow(label, cq, null);
	}

	/**
	 * 报告总结的标题行,即 th
	 * 
	 * @param label 标题名
	 * @param cq 表格横跨多少列
	 * @param id 行id
	 */
	private void titleRow(String label, int cq, String id) {
		m_out.print("<tr");
		if (id != null) {
			m_out.print(" id=\"" + id + "\"");
		}
		m_out.println("><th colspan=\"" + cq + "\">" + label + "</th></tr>");
		m_row = 0;
	}
	/**
	 * 报告总结的标题行,且文本左对齐
	 * 
	 * @param label 标题名
	 * @param cq 表格横跨多少列
	 * @param id 行id
	 */
	private void titleRowTextLeft(String label, int cq, String id) {
		m_out.print("<tr");
		if (id != null) {
			m_out.print(" id=\"" + id + "\" ");
		}
		m_out.println("style=\"text-align:left;\"><th colspan=\"" + cq + "\">" + label + "</th></tr>");
		m_row = 0;
	}

	/** Starts HTML stream */
	protected void startHtml(PrintWriter out) {
		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
		out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		out.println("<head>");
		out.println("<title>TestNG Report</title>");
		out.println("<style type=\"text/css\">");
		out.println("table {margin-bottom:10px;border-collapse:collapse;empty-cells:show}");
		out.println("td,th {border:1px solid #009;padding:.25em .5em}");
		out.println(".result th {vertical-align:bottom}");
		out.println(".param th {padding-left:1em;padding-right:1em}");
		out.println(".param td {padding-left:.5em;padding-right:2em}");
		out.println(".stripe td,.stripe th {background-color: #E6EBF9}");
		out.println(".numi,.numi_attn {text-align:right}");
		out.println(".total td {font-weight:bold}");
		out.println(".passedodd td {background-color: #0A0}");
		out.println(".passedeven td {background-color: #3F3}");
		out.println(".skippedodd td {background-color: #CCC}");
		out.println(".skippedodd td {background-color: #DDD}");
		out.println(".failedodd td,.numi_attn {background-color: #F33}");
		out.println(".failedeven td,.stripe .numi_attn {background-color: #D00}");
		out.println(".stacktrace {white-space:pre;font-family:monospace}");
		out.println(".totop {font-size:85%;text-align:center;border-bottom:0px solid #000}");
		out.println("</style>");
		out.println("</head>");
		out.println("<body>");
	}

	/** Finishes HTML stream */
	protected void endHtml(PrintWriter out) {
		out.println("</body></html>");
	}

	// ~ Inner Classes --------------------------------------------------------
	/** 通过类名和方法名将测试方法排序 */
	private class TestSorter implements Comparator<IInvokedMethod> {
		// ~ Methods
		// -------------------------------------------------------------

		/** Arranges methods by classname and method name */
		public int compare(IInvokedMethod o1, IInvokedMethod o2) {
			// System.out.println("Comparing " + o1.getMethodName() + " " +
			// o1.getDate()
			// + " and " + o2.getMethodName() + " " + o2.getDate());
			return (int) (o1.getDate() - o2.getDate());
			// int r = ((T) o1).getTestClass().getName().compareTo(((T)
			// o2).getTestClass().getName());
			// if (r == 0) {
			// r = ((T) o1).getMethodName().compareTo(((T) o2).getMethodName());
			// }
			// return r;
		}
	}

	// ~ JavaDoc-specific Methods
	// --------------------------------------------------------
	/**
	 * Get ITestNGMethod author(s) string, or class author(s) if no method
	 * author is present. Default return value is "unknown".
	 * 
	 * @param className
	 * @param method
	 * @return
	 * @author hzjingcheng
	 */
	private String getAuthors(String className, ITestNGMethod method) {
		JavaClass cls = builder.getClassByName(className);
		DocletTag[] authors = cls.getTagsByName("author");
		// get class authors as default author name
		String allAuthors = "";
		if (authors.length == 0) {
			allAuthors = "unknown";
		} else {
			for (DocletTag author : authors) {
				allAuthors += author.getValue() + " ";
			}
		}
		// get method author name
		JavaMethod[] mtds = cls.getMethods();
		for (JavaMethod mtd : mtds) {
			if (mtd.getName().equals(method.getMethodName())) {
				authors = mtd.getTagsByName("author");
				if (authors.length != 0) {
					allAuthors = "";
					for (DocletTag author : authors) {
						allAuthors += author.getValue() + " ";
					}
				}
				break;
			}
		}
		return allAuthors.trim();
	}

	/**
	 * Get comment string of Java class.
	 * 
	 * @param className
	 * @return
	 * @author hzjingcheng
	 */
	@SuppressWarnings("unused")
	private String getClassComment(String className) {
		JavaClass cls = builder.getClassByName(className);
		return cls.getComment();
	}

	/**
	 * Get ITestResult id by class + method + parameters hash code.
	 * 获取单个test结果的id，id采用类名+方法名+参数
	 * 
	 * @param result
	 * @return
	 * @author kevinkong
	 */
	private int getId(ITestResult result) {
		int id = result.getTestClass().getName().hashCode();     //测试类名的hash
		id = id + result.getMethod().getMethodName().hashCode();   //测试方法名的hash
		id = id + (result.getParameters() != null ? Arrays.hashCode(result.getParameters()) : 0);  //测试参数的hash
		return id;  
	}

	/**
	 * Get All tests id by class + method + parameters hash code. <p>
	 * 获取所有test结果的id
	 * 
	 * @param context
	 * @param suite
	 * @author kevinkong
	 */
	private void getAllTestIds(ITestContext context, ISuite suite) {
		IResultMap passTests = context.getPassedTests();  //测试通过的测试集
		IResultMap failTests = context.getFailedTests();
		List<IInvokedMethod> invokedMethods = suite.getAllInvokedMethods(); 
		for (IInvokedMethod im : invokedMethods) {
			if (passTests.getAllMethods().contains(im.getTestMethod()) || failTests.getAllMethods().contains(im.getTestMethod())) {   //通过判断被调用的方法是不是在pass或fail的用例中
				int testId = getId(im.getTestResult());
				// m_out.println("ALLtestid=" + testId);
				allRunTestIds.add(testId);
			}
		}
	}
}
