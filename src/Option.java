

/**
 * This is an Option for the {@link lso.OptionParser}
 * 
 * @see OptionParser
 * 
 * @author Zoran Zaric <zz@zoranzaric.de>
 * 
 */
public class Option {
	private String shortOption;
	private String longOption;
	private String description;
	private String value;
	private boolean required;
	private boolean flag;
	private int count;

	public void setShortOption(String shortOption) {
		this.shortOption = shortOption;
	}

	public String getShortOption() {
		return shortOption;
	}

	public void setLongOption(String longOption) {
		this.longOption = longOption;
	}

	public String getLongOption() {
		return longOption;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setRequired(boolean isRequired) {
		this.required = isRequired;
	}

	public boolean isRequired() {
		return required;
	}

	public void setFlag(boolean isFlag) {
		this.flag = isFlag;
	}

	public boolean isFlag() {
		return flag;
	}

	private void setCount(int count) {
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public void incrementCount() {
		this.count += 1;
	}

	public Option(String shortOption, String longOption, String description,
			boolean isRequired, boolean isFlag) {
		this.setShortOption(shortOption);
		this.setLongOption(longOption);
		this.setDescription(description);
		this.setRequired(isRequired);
		this.setFlag(isFlag);

		this.setValue(null);
		this.setCount(0);
	}

	@Override
	public String toString() {
		return this.getShortOption() + ", " + this.getLongOption() + ", "
				+ this.getDescription();
	}
}
