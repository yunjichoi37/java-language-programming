package assign2;

import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

public class Calculator extends Frame {
	
	// 사용자의 눈에 보여질 계산기 화면 구현
	private Frame frame;
	private TextField inputDisplay;
	private String inTopost = ""; // infix에서 postfix로 변환하기 전에 쓸 문자열
	
	private boolean singleOp = false; // 8-16번 기준은 단일연산이기 때문에 따로 간단하게 처리할 예정
	private boolean frontOp = false; // 버튼에서 입력 받을 때 연산자가 연속적으로 오는지 확인
	private boolean Op_minus = false; // -가 음수 기호인 상황에 새로운 연산자가 들어왔을 때, 연산자들을 없애야 할 때 쓸 변수 
	private boolean Op_point = false; // 3.*.4 같은 입력을 해결하기 위해 '.' 다음에 연산자가 오면 사이에 0을 넣어주려 함. 
	
	public Calculator() {
		frame = new Frame("Calculator");
        frame.setSize(600, 350);
        
        WindowDestroyer listener = new WindowDestroyer();
        frame.addWindowListener(listener);
        
        inputDisplay = new TextField();
        inputDisplay.setEditable(false);
        inputDisplay.setFont(new Font("Arial", Font.BOLD, 28));
        inputDisplay.setText("0");
        
        Panel buttonPanel = new Panel();
        buttonPanel.setLayout(new GridLayout(5, 6, 5, 5));
        
        String[] buttonNames = {
        		" ", "x!", "(", ")", "%", "AC",
                "sin", "ln", "7", "8", "9", "/",
                "cos", "log", "4", "5", "6", "*",
                "tan", "√", "1", "2", "3", "-",
                " ", "x^y", "0", ".", "=", "+"
        };
        
        for (String name : buttonNames) { // 편리한 for문 기능 사용 ㅎㅎ ㅎ
        	Button bt = new Button(name);
        	if (name.equals("=")) {
        		bt.setBackground(new Color(83, 131, 236));
        		bt.setForeground(Color.WHITE);
        	} else if (name.equals(".") || name.equals("0") || name.equals("1") || name.equals("2") 
        			|| name.equals("3") || name.equals("4") || name.equals("5") || name.equals("6") 
        			|| name.equals("7") || name.equals("8") || name.equals("9")) {
				bt.setBackground(new Color(241, 243, 244));
        	} else {
        		bt.setBackground(new Color(218, 220, 224));
        	}
        	bt.setFont(new Font("Arial", Font.PLAIN, 20));
        	bt.addActionListener(new ButtonClick());
        	buttonPanel.add(bt);
        }
        
        frame.add(inputDisplay, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.setVisible(true);
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	
	public class WindowDestroyer extends WindowAdapter { // x 눌렀을 때 없어지게
	    public void windowClosing(WindowEvent e) {
	        System.exit(0);
	    }
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	
	private class ButtonClick implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String token = e.getActionCommand();
			switch (token) {
			case "AC": // 초기화 과정. boolean 초기화 잊지 말기
				inputDisplay.setText("0"); 
				inTopost = "";
				singleOp = false; 
				frontOp = false;
				Op_minus = false;
				Op_point = false;
				break;
			case "=": // 이 입력이 들어올 때 infix를 postfix로 바꾸면 된다.

				if (singleOp == true) { // 단일연산 식이 들어오면 postfix로 바꾸지 않고 바로 계산한다.
					Number result;
					result = singleOperations(inTopost); // 단일연산기 호출
					
					// 특수 경우(ln0, log0 등)엔 그대로 멈춰주기
					if (result.doubleValue() == Double.MAX_VALUE) break;
					
					inputDisplay.setText(String.valueOf(result));
					break;
				}
				
				String postfix = convertToPostfix(inTopost); // 단일연산이 아닌 경우 postfix로 변환 
				Number result = calc(postfix); // 변환된 문자열 계산하기
				
				if (result.doubleValue() == Double.MAX_VALUE) break;
				// result의 더블 벨류가 Double.MAX_VALUE라면 0으로 나눴다는 뜻이다.
				// break를 써 inputDisplay에 표시된 것을 바꾸지 않고 마무리
				
				inputDisplay.setText(String.valueOf(result));
				break;
			case "x!": // display에 ! 만 띄우고 싶했습니다. 
				inTopost += "!";
				if (inTopost.equals("!")) // 숫자 입력 없이 0이 바로 들어올 경우
					inTopost = "0!";
				inputDisplay.setText(inTopost);
				singleOp = true;
				break;
			case "x^y": // display에 ^ 만 띄우고 싶했습니다.
				inTopost += "^";
				inputDisplay.setText(inTopost);
				singleOp = true;
				break;
			case "%": // 나머지 단일 연산
				inTopost += "%";
				if (inTopost.equals("%"))
					inTopost = "0%"; // 숫자 입력 없이 0이 바로 들어올 경우
				inputDisplay.setText(inTopost);
				singleOp = true;
				break;
			case "log": // 나머지 단일 연산
			case "ln":
			case "√":
			case "cos":
			case "sin":
			case "tan":
				inTopost += token;
				inputDisplay.setText(inTopost);
				singleOp = true;
				break;
			case ".":
				if (inTopost.equals("")) { // AC 상태로 . 만 눌렀을 때
					inputDisplay.setText("0.");
					inTopost = "0"; // .은 공통적으로 추가할 것
				} else if (frontOp == true) { // . 찍었는데 앞에가 연산자일 때
					inTopost += "0";					
				}
				Op_point = true; // . 연산자가 앞에 있다고 표시해주기.
				
				inTopost += token; // 이제 . 추가하기
				inputDisplay.setText(inTopost);
				break;
			case "/":
				if (inTopost.equals("")) { // 처음에 아무것도 없어도 누르면 0이 뜨게끔
					inTopost += "0";
					inputDisplay.setText("0/");
				}
			case "*":
				if (inTopost.equals("")) { 
					inTopost += "0";
					inputDisplay.setText("0*");
				}
			case "+":
				if (inTopost.equals("")) { 
					inTopost += "0";
					inputDisplay.setText("0+");
				}
			case "-":				
				if (Op_minus == true) {
					// -가 음수처리 기호인 상황에 새로운 연산자가 들어오면 기존 입력되었던 연산자들을 없애야 함.
					// len(inTopost)-2 가 되는 까닭은 '연산자' + '-' 가 가능한 제일로 긴 연산자 입력이기 때문 
					inTopost = inTopost.substring(0, inTopost.length() - 2); 
					frontOp = false;
					Op_minus = false;
				}
				if (Op_point == true) {
					inTopost += "0"; // 연산자 앞에 .이 있으면 0을 추가하고 다시 false로 바꿔주기
					Op_point = false;
				}				
				
				if (inTopost.length() != 0) {
					if (inTopost.charAt(inTopost.length() - 1) == '+') { // - 다음에 +가 오면 -만 뜨게 처리해야 함
						inTopost = inTopost.substring(0, inTopost.length() - 1) + "-"; // 맨 마지막 거(+) 빼고 - 넣어주기
						inputDisplay.setText(inTopost);
						frontOp = true; // 앞에가 연산자(-)라고 표시
						break;
					}
				}
				
				inTopost += token; // 이제 본격적으로 계산식 만들기
				
				if (frontOp == true) { // 앞에가 연산자인 경우, 
					// 현재 case /, *, +에서 break를 안 했기 때문에 token으로 올 수 있음
					
					if (token == "-") { // 그 상황에서 들어온 input이 -라면 음수처리를 해야 한다.
						
						Op_minus = true; // 음수처리에 쓰임을 알 수 있게 해주는 불리안 변수						
						inputDisplay.setText(inTopost);
						break;
					}
					inTopost = inTopost.substring(0, inTopost.length() - 2) + token; // 그 전 연산자 빼고 방금 누른 거 추가하기
				}
				inputDisplay.setText(inTopost);
				frontOp = true; // 앞에가 연산자임을 알려주기 위해 이제 true로 바꿔주기
				break;
			case "0":
				if (inTopost.equals("") || inTopost.equals("0")) { // 00000을 눌러도 0만 표시되게 하기 
					inTopost = "0";
					inputDisplay.setText("0");
					break;
				} // if문 밖에 break를 두지 않음으로써, 첫 입력이 아닐 경우엔 default로 넘어가게끔 코딩하였다.
			default:				
				frontOp = false; // 입력으로 숫자가 들어오면 다시 false 처리 꼭 해줘야 함.
                Op_minus = false;
                Op_point = false;
                
                if(inTopost.equals("0")) { // 0 입력 후 다른 숫자를 입력했을 때 그 숫자가 뜨게 바꿔줌.
                	inTopost = token; // 첫 입력으로 들어오는 0을 아예 무시하는 방법도 있지만 연산자를 처음에 바로 입력했을 때 0이 뜨게 하기 위해 이렇게 구상함.
                } else
                	inTopost += token;
				inputDisplay.setText(inTopost);
                
                break;
			}
		}
	}
		
	/////////////////////////////////////////////////////////////////////////////////
	
	// (, )는 따로 완전히 처리되게 설정하고, 여기서는 아래 것들만 설정하면 된다.
	private int precedence(char operator) {
		switch (operator) {
		case '+': // 우선순위가 낮은 애들
		case '-':
			return 1;
		case '*':
		case '/':
			return 2;
		default: // %, x!, log, ln, root, ^, cos, sin, tan 은 다 단일연산이니까 -1로 퉁치자
			return -1;
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	
	private String convertToPostfix(String infix) {
		
		Stack<Character> stack = new Stack<>();
		String postfix = new String();
		
		int i = 0;
		while(i < infix.length()) {
			
			char symbol = infix.charAt(i);

			if (symbol == '(') { // 우선순위 연산하기
				stack.push(symbol); 
			} else if (symbol == ')') {
				while (!stack.isEmpty() && stack.peek() != '(') { 
					postfix += stack.pop();
					postfix += " "; // 띄어쓰기 넣어주기. 나중에 계산할 때 split을 쓰기 위해
				}
				stack.pop();
			} else if (symbol == '+' || symbol == '-' || symbol == '*' || symbol == '/') {
				// 음수인지 먼저 확인하기: -가 첫 입력이거나, - 앞에 (, /, *가 있을 경우
				if (symbol == '-' && (i == 0 || infix.charAt(i - 1) == '(' || infix.charAt(i - 1) == '*' || infix.charAt(i - 1) == '/' || infix.charAt(i - 1) == '-')) {
					postfix += symbol; // 음수 부호로 간주하기
					i++;
					
					while (i < infix.length() && Character.isDigit(infix.charAt(i))) {
						postfix += infix.charAt(i++); // 음수 범위에 있는 애들 음수로 추가하기
					}
					postfix += " "; 
					continue;
				}
				
				while (!stack.isEmpty() && precedence(stack.peek()) >= precedence(symbol)) {
					postfix += stack.pop();
					postfix += " ";
				}
				stack.push(symbol);
			} else if('0' <= symbol && symbol <= '9') { // 두 자릿수 이상 숫자 처리 과정
				do {
					postfix += symbol;
					i++;
					if (i < infix.length()) {
						symbol = infix.charAt(i);
					} else {
						break; // 돌다가 문자열 끝에 오면 멈추기					
					}
				} while ('0' <= symbol && symbol <= '9');
				
				postfix += " ";
				i--; // do while 문에서 한번 더 증가한 거 꼭 감소시켜줘야 함.
			} else if (symbol == '.') { // .이 오면 소수로 처리되게끔 숫자와 연속적으로 붙여야 함. (띄어쓰기 x)
				postfix = postfix.substring(0, postfix.length() - 1) + ".";
			}
			i++; // 다음 인덱스로 이동하기
		}
		
		while (!stack.isEmpty()) {
			postfix += stack.pop();
			postfix += " ";
		}
		
		return postfix;
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	
	private Number singleOperations(String s) { // 단일연산 처리하기
		
		if (s.contains("%")) { // =을 누른 후 전달 된 s에 단일연산자가 있으면
			int num = Integer.parseInt(s.substring(0, s.indexOf("%")));
			double result = (double) num / 100;
			return result;
		} 
		else if (s.contains("!") ) {
			int num = Integer.parseInt(s.substring(0, s.indexOf("!")));
			int result = 1;
			for (int i = 1; i <= num; i++) // 팩토리얼 계산
				result *= i;
			return result;
		} 
		else if (s.contains("log") ) { // "log -> 숫자 -> =" 순으로 작동하게 만들었습니다.
			int num = Integer.parseInt(s.substring(3, s.length())); // 앞 세 글자는 log
			double result = Math.log10(num);
			
			if (num == 0) {
				inputDisplay.setText("-Infinity");
				return Double.MAX_VALUE;
			}
			
			if (result == Math.floor((double) result)) // 계산했더니 정수로 나오면 정수로 반환
				return (int) result;
			result = Math.round(result * 1000000) / 1000000.0;
			return result;
		} 
		else if (s.contains("ln") ) { // "ln -> 숫자 -> ="
			int num = Integer.parseInt(s.substring(2, s.length())); // 앞 두 글자는 ln
			
			if (num == 0) {
				inputDisplay.setText("-Infinity");
				return Double.MAX_VALUE;
			}			
			
			double result = Math.log(num);
			result = Math.round(result * 1000000) / 1000000.0;
			return result; // 이 계산기 내에서 ln은 정수로 나올 수 없으니 바로 return하기
		} 
		else if (s.contains("√") ) { // "√ -> 숫자 -> ="
			int num = Integer.parseInt(s.substring(1, s.length()));
			double result = Math.sqrt(num);
			
			if (num < 0) { // 음수인 경우 에러 처리
				inputDisplay.setText("Error");
				return Double.MAX_VALUE;
			}
			
			if (result == Math.floor((double) result))
				return (int) result;
			result = Math.round(result * 1000000) / 1000000.0;
			return result;
		} 
		else if (s.contains("^") ) { // "숫자 -> ^ -> 숫자 -> ="
			int num1 = Integer.parseInt(s.substring(0, s.indexOf("^")));
			int num2 = Integer.parseInt(s.substring(s.indexOf("^") + 1, s.length()));
			
			double result = Math.pow(num1, num2);
			return (int) result;
		}
		else if (s.contains("cos") ) { // "cos -> 숫자 -> ="
			double num = Integer.parseInt(s.substring(3, s.length()));
			num = Math.toRadians(num); // degree로 입력해도 잘 처리되도록
			
			double result = Math.cos(num);
			result = Math.round(result * 1000000) / 1000000.0;
			return result;
		}
		else if (s.contains("sin") ) { // "sin -> 숫자 -> ="
			double num = Integer.parseInt(s.substring(3, s.length()));
			num = Math.toRadians(num); // degree로 입력해도 잘 처리되도록
			
			double result = Math.sin(num);
			result = Math.round(result * 1000000) / 1000000.0;
			return result;
		}
		else if (s.contains("tan") ) { // "tan -> 숫자 -> ="
			double num = Integer.parseInt(s.substring(3, s.length()));
			num = Math.toRadians(num); // degree로 입력해도 잘 처리되도록
			
			// num으로 90, 270, -90이 들어올 때는 에러 처리 해주기
			if ((num % Math.PI == Math.PI / 2) || (num % Math.PI == -Math.PI / 2)) {
				inputDisplay.setText("Error");
				return Double.MAX_VALUE;
			}
			
			double result = Math.tan(num);
			result = Math.round(result * 1000000) / 1000000.0;
			return result;
		}
		return null;
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	
	private Number calc(String postfix) {
		double value, op1, op2;
		int i;
		
		String[] tokenList = postfix.split(" "); // 앞서 postfix를 만들 때 요소 사이에 공백이 오게 지정해둠. 다시 찢기
		Stack<Double> stack = new Stack<>();
		
		for (String token : tokenList) { 
			if (isNumber(token)) { // 숫자인가 확인하기
				value = Double.parseDouble(token);
				stack.push(value);
			} else { // 연산자인 경우
				op2 = stack.pop(); // op2를 먼저 pop해야 한다(stack 특성)
				op1 = stack.pop();
				
				switch (token) {
				case "+":
					stack.push(op1 + op2);
					break;
				case "-":
					stack.push(op1 - op2);
					break;
				case "*":
					stack.push(op1 * op2);
					break;
				case "/":
					if (op2 == 0) { // 0으로 나눌 수 없다는 표시를 해줘야 한다.
						if (op1 > 0) {
							inputDisplay.setText("Infinity");
							return Double.MAX_VALUE;
						} else if (op1 < 0) {
							inputDisplay.setText("-Infinity");
							return Double.MAX_VALUE;
						} else {
							inputDisplay.setText("Error");
							return Double.MAX_VALUE;
						}
					}
					stack.push(op1 / op2);
					break;
				default: // 입력이 잘 되지 않았을 때 예외처리를 해줘야 함.
					throw new IllegalArgumentException("유효하지 않은 입력입니다(" + token + ")");
				}
			}			
		}
		
		double result = stack.pop();
		
		if (result == Math.floor(result)) { // 정수로 답이 나오면
			return (int) result;
		} else { // 소수로 나오면 소수점 6자리까지
			result = Math.round(result * 1000000) / 1000000.0;
			return result;
		}		
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	
	private boolean isNumber(String s) { // split해서 들어온 요소가 숫자인가 확인하기
		try { // Double로 변환할 수 없으면 Exception이 발생하는 점을 이용하기 
			Double.parseDouble(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////

	public static void main(String[] args) {
		new Calculator();
		// 채점해주셔서 감사합니다!!
	}
}