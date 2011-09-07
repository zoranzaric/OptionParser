

import java.util.HashMap;
import java.util.Vector;

/**
 * An option parser that is used to parse commandline arguments. It is
 * configured using {@Link lso.Option} objects.
 * 
 * @see Option
 * 
 * @author Zoran Zaric <zz@zoranzaric.de>
 * 
 */
public class OptionParser {
	private Option[] acceptedOptions;
	private Option[] parsedOptions;

	private String usageIntroduction;
	private int verbosityLevel;
	private boolean quiet;

	private HashMap<String, Option> optionsByShortOption;
	private HashMap<String, Option> optionsByLongOption;

	private void setUsageIntroduction(String usageIntroduction) {
		this.usageIntroduction = usageIntroduction;
	}

	public String getUsageIntroduction() {
		return usageIntroduction;
	}

	private void setVerbosityLevel(int verbosityLevel) {
		this.verbosityLevel = verbosityLevel;
	}

	public int getVerbosityLevel() {
		return ((!this.isQuiet()) ? verbosityLevel : 0);
	}

	private void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}

	public boolean isQuiet() {
		return quiet;
	}

	public Option[] getOptions() {
		return this.parsedOptions;
	}

	public Option getByShortOption(String shortOption) {
		return this.optionsByShortOption.get(shortOption);
	}

	public Option getByLongOption(String longOption) {
		return this.optionsByLongOption.get(longOption);
	}

	public OptionParser(Option[] acceptedOptions, String usageIntroduction) {
		this.setUsageIntroduction(usageIntroduction);
		this.setQuiet(false);
		this.setVerbosityLevel(0);

		this.optionsByShortOption = new HashMap<String, Option>();
		this.optionsByLongOption = new HashMap<String, Option>();

		Vector<Option> addedAcceptedOptions = new Vector<Option>();

		for (Option option : acceptedOptions) {
			boolean noOption = true;
			if (option.getShortOption() != null) {
				if (!this.optionsByShortOption.containsKey(option
						.getShortOption())) {
					this.optionsByShortOption.put(option.getShortOption(),
							option);
					noOption = false;
				} else {
					throw new IllegalArgumentException(
							"Options must only exist once!");
				}
			}

			if (option.getLongOption() != null) {
				if (!this.optionsByLongOption.containsKey(option
						.getLongOption())) {
					this.optionsByLongOption
							.put(option.getLongOption(), option);
					noOption = false;
				} else {
					throw new IllegalArgumentException(
							"Options must only exist once!");
				}
			}

			if (noOption) {
				throw new IllegalArgumentException(
						"At least either a short or a long option has to be given!");
			} else {
				addedAcceptedOptions.add(option);
			}
		}

		Option[] addedOptionsArray = new Option[addedAcceptedOptions.size()];
		for (int i = 0; i < addedAcceptedOptions.size(); i++) {
			addedOptionsArray[i] = addedAcceptedOptions.get(i);
		}

		this.acceptedOptions = addedOptionsArray;
	}

	public void parse(String[] args) {
		Vector<Option> parsedOptions = new Vector<Option>();

		for (int i = 0; i < args.length; i++) {
			Option option = null;

			if (args[i].equals("-h") || args[i].equals("--help")) {
				System.err.println(this.getUsage());
				System.exit(0);
			}

			if (args[i].equals("-q") || args[i].equals("--quiet")) {
				this.setQuiet(true);
			}

			if (args[i].equals("-v") || args[i].equals("--verbose")) {
				this.setVerbosityLevel(this.verbosityLevel + 1);
			}

			if (args[i].substring(0, 2).equals("--")) {
				String longOption = args[i].substring(2);
				if (this.optionsByLongOption.containsKey(longOption)) {
					option = this.optionsByLongOption.get(longOption);
				} else {
					System.err.println("Unkown long option <" + longOption
							+ ">");
				}
			} else if (args[i].substring(0, 1).equals("-")) {
				String shortOption = args[i].substring(1);
				if (this.optionsByShortOption.containsKey(shortOption)) {
					option = this.optionsByShortOption.get(shortOption);
				} else {
					System.err.println("Unkown short option <" + shortOption
							+ ">");
				}

			}

			if (option != null) {
				option.incrementCount();

				if (!option.isFlag()) {
					option.setValue(args[i + 1]);
					i++;
				}

				parsedOptions.add(option);
			}
		}

		Vector<Option> errors = new Vector<Option>();

		for (Option option : this.acceptedOptions) {
			if (option.isRequired() && option.getCount() == 0) {
				errors.add(option);
			}
		}

		if (errors.size() != 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("Options required but not given:\n");
			for (Option errorOption : errors) {
				sb.append("  " + errorOption + "\n");
			}
			sb.append("\n" + this.getUsage());

			throw new IllegalArgumentException(sb.toString());
		}

		Option[] parsedOptionsArray = new Option[parsedOptions.size()];
		for (int i = 0; i < parsedOptions.size(); i++) {
			parsedOptionsArray[i] = parsedOptions.get(i);
		}

		this.parsedOptions = parsedOptionsArray;
	}

	public String getUsage() {
		StringBuilder sb = new StringBuilder();
		sb.append("Usage: " + this.getUsageIntroduction() + "\n\n");

		Vector<Option> requiredOptions = new Vector<Option>();
		Vector<Option> optionalOptions = new Vector<Option>();
		int longestLongOptionLenght = 0;
		for (Option option : this.acceptedOptions) {
			if (option.getLongOption().length() > longestLongOptionLenght) {
				longestLongOptionLenght = option.getLongOption().length();
			}
			if (option.isRequired()) {
				requiredOptions.add(option);
			} else {
				optionalOptions.add(option);
			}
		}

		if (requiredOptions.size() > 0) {
			sb.append("Required parameters:\n");
			for (Option option : requiredOptions) {
				sb.append("  -" + option.getShortOption() + ", ");
				sb.append("  --"
						+ String.format("%1$-" + longestLongOptionLenght + "s",
								option.getLongOption()) + "\t");
				sb.append(option.getDescription() + "\n");
			}
		}

		if (optionalOptions.size() > 0) {
			sb.append("Optional parameters:\n");
			for (Option option : optionalOptions) {
				sb.append("  -" + option.getShortOption() + ", ");
				sb.append("  --"
						+ String.format("%1$-" + longestLongOptionLenght + "s",
								option.getLongOption()) + "\t");
				sb.append(option.getDescription() + "\n");
			}
		}

		return sb.toString();
	}
}
