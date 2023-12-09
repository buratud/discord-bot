package interactions;

import com.buratud.interactions.ChatGpt;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChatGptTests {
    @Test
    public void testSplitResponse1() {
        String original = """
                Fundamental data typically refers to economic indicators, financial statements, and other quantitative data that investors use to evaluate the intrinsic value of a company's stock or the overall health of an economy. However, for Bitcoin (BTC) and other cryptocurrencies, the concept of "fundamentals" is a bit different since they do not have financial statements, nor are they companies with revenues, earnings, or traditional business models.
                For BTCUSD (Bitcoin to US Dollar exchange rate), fundamental data would consist of different types of information that could potentially influence the price of Bitcoin. Some of these data points include:
                1. **Supply Data:**
                   - **Total Supply:** The capped total supply of Bitcoin, which is 21 million coins.
                   - **Mining Rate:** The rate at which new bitcoins are created through mining, which halves approximately every four years in an event known as the "halving."
                2. **Adoption and Demand:**
                   - The number of active Bitcoin wallets, growth in the number of transactions, and average transaction value.
                   - The rate of adoption by consumers, businesses, and financial institutions.
                3. **Regulation:**
                   - Legal developments surrounding the use, trading, or taxation of Bitcoin in various countries can greatly affect its price.
                   - Policy changes by central banks or government bodies that could impact the cryptocurrency ecosystem.
                4. **Market Sentiment:**
                   - Investor sentiment and market trends as reflected through various sentiment indicators or social media analysis.
                   - News and events, such as technological advancements, security breaches, or high-profile endorsements.
                5. **Technical Infrastructure:**
                   - The development and implementation of updates to Bitcoin’s protocol (e.g., SegWit, Taproot).
                   - The security and stability of the underlying blockchain technology.
                6. **Economic Indicators:**
                   - The state of the global economy, inflation rates, and currency devaluation, which can influence Bitcoin’s attractiveness as a store of value or hedge against inflation.
                   - Central banks' monetary policies, particularly those of the U.S. Federal Reserve, which can affect the value of fiat currencies compared to Bitcoin.
                7. **Market Liquidity and Volume:**
                   - Trading volume on exchanges and fluctuations in liquidity can impact price discovery and volatility.
                8. **Competing Cryptocurrencies:**
                   - Developments and popularity of other cryptocurrencies may affect Bitcoin’s market share and investor interest.
                9. **Network Performance:**
                   - Metrics such as hash rate (a measure of computational power devoted to mining Bitcoin) and network difficulty can indicate the security and competitiveness of mining.
                Investors looking at the fundamentals of BTCUSD would analyze these factors to try to predict how they might influence demand for Bitcoin, and thus its price relative to the USD. However, it's important to note that the cryptocurrency market is highly speculative and can be influenced by many unpredictable factors, making it a very volatile and complex asset class.""";
        List<String> expected = List.of("""
                        Fundamental data typically refers to economic indicators, financial statements, and other quantitative data that investors use to evaluate the intrinsic value of a company's stock or the overall health of an economy. However, for Bitcoin (BTC) and other cryptocurrencies, the concept of "fundamentals" is a bit different since they do not have financial statements, nor are they companies with revenues, earnings, or traditional business models.
                        For BTCUSD (Bitcoin to US Dollar exchange rate), fundamental data would consist of different types of information that could potentially influence the price of Bitcoin. Some of these data points include:
                        1. **Supply Data:**
                           - **Total Supply:** The capped total supply of Bitcoin, which is 21 million coins.
                           - **Mining Rate:** The rate at which new bitcoins are created through mining, which halves approximately every four years in an event known as the "halving."
                        2. **Adoption and Demand:**
                           - The number of active Bitcoin wallets, growth in the number of transactions, and average transaction value.
                           - The rate of adoption by consumers, businesses, and financial institutions.
                        3. **Regulation:**
                           - Legal developments surrounding the use, trading, or taxation of Bitcoin in various countries can greatly affect its price.
                           - Policy changes by central banks or government bodies that could impact the cryptocurrency ecosystem.
                        4. **Market Sentiment:**
                           - Investor sentiment and market trends as reflected through various sentiment indicators or social media analysis.
                           - News and events, such as technological advancements, security breaches, or high-profile endorsements.
                        5. **Technical Infrastructure:**
                           - The development and implementation of updates to Bitcoin’s protocol (e.g., SegWit, Taproot).
                           - The security and stability of the underlying blockchain technology.""",
                """
                        6. **Economic Indicators:**
                           - The state of the global economy, inflation rates, and currency devaluation, which can influence Bitcoin’s attractiveness as a store of value or hedge against inflation.
                           - Central banks' monetary policies, particularly those of the U.S. Federal Reserve, which can affect the value of fiat currencies compared to Bitcoin.
                        7. **Market Liquidity and Volume:**
                           - Trading volume on exchanges and fluctuations in liquidity can impact price discovery and volatility.
                        8. **Competing Cryptocurrencies:**
                           - Developments and popularity of other cryptocurrencies may affect Bitcoin’s market share and investor interest.
                        9. **Network Performance:**
                           - Metrics such as hash rate (a measure of computational power devoted to mining Bitcoin) and network difficulty can indicate the security and competitiveness of mining.
                        Investors looking at the fundamentals of BTCUSD would analyze these factors to try to predict how they might influence demand for Bitcoin, and thus its price relative to the USD. However, it's important to note that the cryptocurrency market is highly speculative and can be influenced by many unpredictable factors, making it a very volatile and complex asset class.""");
        List<String> actual = ChatGpt.splitResponse(original);
        assertEquals(expected, actual);
    }

    @Test
    public void testSplitResponse2() {
        String original = """
                Sure! Here's a simple Python code that prints "Hello world" as an MD5 headline:
                ```python
                import hashlib
                # String to be encoded in MD5
                message = "Hello world"
                # Encoding the string in MD5
                md5_hash = hashlib.md5(message.encode()).hexdigest()
                # Print the MD5 hash as headline
                print("# " + md5_hash)
                ```""";
        List<String> expected = List.of(original);
        List<String> actual = ChatGpt.splitResponse(original);
        assertEquals(expected, actual);
    }

    @Test
    public void testSplitResponse3() {
        String original = """
                My apologies for misunderstanding your original request. Here is an updated Python algorithm that takes into account new lines, lists, and code blocks when splitting a long text into chunks of 2000 characters:
                ```python
                def split_text(text):
                    max_length = 2000
                    chunks = []
                    current_chunk = ""
                    lines = text.split('\\n')
                    current_chunk_length = 0
                    for line in lines:
                        if line.startwith('-') or line.startswith('*')):
                            chunks.append(current_chunk)
                            current_chunk = ""
                            current_chunk_length = 0
                        if line.startswith('```'):
                            if current_chunk:
                                chunks.append(current_chunk)
                                current_chunk = ""
                                current_chunk_length = 0
                            chunks.append(line + '\\n')
                        elif line.startswith('*'):
                            if current_chunk:
                                chunks.append(current_chunk)
                                current_chunk = ""
                                current_chunk_length = 0
                            current_chunk = line + '\\n'
                            chunks.append(current_chunk)
                            current_chunk = ""
                            current_chunk_length = 0
                        else:
                            if len(line) > max_length:
                                index = 0
                                while index < len(line):
                                    chunks.append(line[index:index+max_length])
                                    index += max_length
                            else:
                                current_chunk += line + '\\n'
                                current_chunk_length += len(line) + 1
                    if current_chunk:
                        chunks.append(current_chunk)
                    return chunks
                ```
                This algorithm iterates through the lines of the text and splits the text into chunks based on the 2000 characters limit, new lines, lists, and code blocks. The algorithm keeps track of the current chunk length and checks for the start of a code block or a list to appropriately split the text into chunks. It handles long lines of text by breaking them down into multiple chunks of 2000 characters.""";
        List<String> expected = List.of(original);
        List<String> actual = ChatGpt.splitResponse(original);
        assertEquals(expected, actual);
    }
    @Test
    public void testSplitResponse4() {
        String original = """
                A text with Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed et vehicula dui. Cras felis odio, feugiat vel malesuada nec, aliquet non ipsum. Vivamus tempor neque velit, non malesuada ex scelerisque non. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris feugiat nunc elit, sed tincidunt ex interdum non. Duis hendrerit tortor augue, non porta purus fermentum a. Phasellus finibus dui dapibus, egestas quam et, congue magna. Pellentesque ullamcorper est id ligula scelerisque dapibus. Maecenas congue pharetra ex sed accumsan. Vestibulum consequat a neque a malesuada. Phasellus hendrerit, velit at porttitor tristique, orci dolor pretium mi, elementum vestibulum nisi justo in ex. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Vivamus gravida justo ac sodales rutrum. Morbi hendrerit ligula at enim congue, sed feugiat elit congue. Quisque nisi enim, varius sodales elit vitae, porta consequat lorem. Donec eu maximus velit. Mauris et mi orci. Vestibulum efficitur tortor justo, sed convallis quam porta vitae. Donec vitae rutrum arcu. Morbi sit amet pretium nulla. Sed eleifend velit at enim volutpat, nec vehicula eros convallis. Fusce rutrum finibus elit sed elementum. Aenean tincidunt tincidunt odio, vitae feugiat leo laoreet sed. Pellentesque metus turpis, consectetur nec fermentum in, euismod sit amet lectus. Proin a risus neque. Aliquam eu magna ultrices, tincidunt lacus eget, blandit turpis. Nunc libero velit, varius in consequat gravida, egestas eu elit. Aliquam vitae scelerisque neque, sed tincidunt turpis. Donec ut erat lobortis, dignissim justo sit amet, fringilla massa. Etiam vitae elit a nisl mattis dapibus. Duis velit massa, lacinia ut malesuada sit amet, dictum eget metus.
                1. A very long text of Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed et vehicula dui. Cras felis odio, feugiat vel malesuada nec, aliquet non ipsum. Vivamus tempor neque velit, non malesuada ex scelerisque non. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris feugiat nunc elit, sed tincidunt ex interdum non. Duis hendrerit tortor augue, non porta purus fermentum a. Phasellus finibus dui dapibus, egestas quam et, congue magna. Pellentesque ullamcorper est id ligula scelerisque dapibus. Maecenas congue pharetra ex sed accumsan. Vestibulum consequat a neque a malesuada. Phasellus hendrerit, velit at porttitor tristique, orci dolor pretium mi, elementum vestibulum nisi justo in ex. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Vivamus gravida justo ac sodales rutrum. Morbi hendrerit ligula at enim congue, sed feugiat elit congue. Quisque nisi enim, varius sodales elit vitae, porta consequat lorem. Donec eu maximus velit. Mauris et mi orci. Vestibulum efficitur tortor justo, sed convallis quam porta vitae. Donec vitae rutrum arcu. Morbi sit amet pretium nulla. Sed eleifend velit at enim volutpat, nec vehicula eros convallis. Fusce rutrum finibus elit sed elementum. Aenean tincidunt tincidunt odio, vitae feugiat leo laoreet sed. Pellentesque metus turpis, consectetur nec fermentum in, euismod sit amet lectus. Proin a risus neque. Aliquam eu magna ultrices, tincidunt lacus eget, blandit turpis. Nunc libero velit, varius in consequat gravida, egestas eu elit. Aliquam vitae scelerisque neque, sed tincidunt turpis. Donec ut erat lobortis, dignissim justo sit amet, fringilla massa. Etiam vitae elit a nisl mattis dapibus. Duis velit massa, lacinia ut malesuada sit amet, dictum eget metus.""";
        List<String> expected = List.of("A text with Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed et vehicula dui. Cras felis odio, feugiat vel malesuada nec, aliquet non ipsum. Vivamus tempor neque velit, non malesuada ex scelerisque non. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris feugiat nunc elit, sed tincidunt ex interdum non. Duis hendrerit tortor augue, non porta purus fermentum a. Phasellus finibus dui dapibus, egestas quam et, congue magna. Pellentesque ullamcorper est id ligula scelerisque dapibus. Maecenas congue pharetra ex sed accumsan. Vestibulum consequat a neque a malesuada. Phasellus hendrerit, velit at porttitor tristique, orci dolor pretium mi, elementum vestibulum nisi justo in ex. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Vivamus gravida justo ac sodales rutrum. Morbi hendrerit ligula at enim congue, sed feugiat elit congue. Quisque nisi enim, varius sodales elit vitae, porta consequat lorem. Donec eu maximus velit. Mauris et mi orci. Vestibulum efficitur tortor justo, sed convallis quam porta vitae. Donec vitae rutrum arcu. Morbi sit amet pretium nulla. Sed eleifend velit at enim volutpat, nec vehicula eros convallis. Fusce rutrum finibus elit sed elementum. Aenean tincidunt tincidunt odio, vitae feugiat leo laoreet sed. Pellentesque metus turpis, consectetur nec fermentum in, euismod sit amet lectus. Proin a risus neque. Aliquam eu magna ultrices, tincidunt lacus eget, blandit turpis. Nunc libero velit, varius in consequat gravida, egestas eu elit. Aliquam vitae scelerisque neque, sed tincidunt turpis. Donec ut erat lobortis, dignissim justo sit amet, fringilla massa. Etiam vitae elit a nisl mattis dapibus. Duis velit massa, lacinia ut malesuada sit amet, dictum eget metus.",
                "1. A very long text of Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed et vehicula dui. Cras felis odio, feugiat vel malesuada nec, aliquet non ipsum. Vivamus tempor neque velit, non malesuada ex scelerisque non. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris feugiat nunc elit, sed tincidunt ex interdum non. Duis hendrerit tortor augue, non porta purus fermentum a. Phasellus finibus dui dapibus, egestas quam et, congue magna. Pellentesque ullamcorper est id ligula scelerisque dapibus. Maecenas congue pharetra ex sed accumsan. Vestibulum consequat a neque a malesuada. Phasellus hendrerit, velit at porttitor tristique, orci dolor pretium mi, elementum vestibulum nisi justo in ex. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Vivamus gravida justo ac sodales rutrum. Morbi hendrerit ligula at enim congue, sed feugiat elit congue. Quisque nisi enim, varius sodales elit vitae, porta consequat lorem. Donec eu maximus velit. Mauris et mi orci. Vestibulum efficitur tortor justo, sed convallis quam porta vitae. Donec vitae rutrum arcu. Morbi sit amet pretium nulla. Sed eleifend velit at enim volutpat, nec vehicula eros convallis. Fusce rutrum finibus elit sed elementum. Aenean tincidunt tincidunt odio, vitae feugiat leo laoreet sed. Pellentesque metus turpis, consectetur nec fermentum in, euismod sit amet lectus. Proin a risus neque. Aliquam eu magna ultrices, tincidunt lacus eget, blandit turpis. Nunc libero velit, varius in consequat gravida, egestas eu elit. Aliquam vitae scelerisque neque, sed tincidunt turpis. Donec ut erat lobortis, dignissim justo sit amet, fringilla massa. Etiam vitae elit a nisl mattis dapibus. Duis velit massa, lacinia ut malesuada sit amet, dictum eget metus.");
        List<String> actual = ChatGpt.splitResponse(original);
        assertEquals(expected, actual);
    }
}
